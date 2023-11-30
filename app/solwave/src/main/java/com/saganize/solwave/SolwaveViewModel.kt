package com.saganize.solwave

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.saganize.solwave.core.di.TAG
import com.saganize.solwave.core.models.SelectResult
import com.saganize.solwave.core.util.ifError
import com.saganize.solwave.core.util.ifSuccess
import com.saganize.solwave.core.util.showToast
import com.saganize.solwave.core.util.stringify
import com.saganize.solwave.core.util.toName
import com.saganize.solwave.core.util.update
import com.saganize.solwave.data.remote.model.SimulationType
import com.saganize.solwave.data.remote.model.TransactionStatus
import com.saganize.solwave.data.remote.model.requests.InitiateAuthRequest
import com.saganize.solwave.data.remote.model.requests.InitiateTransactionRequest
import com.saganize.solwave.data.remote.model.requests.SimulateTransactionRequest
import com.saganize.solwave.domain.model.TransactionParams
import com.saganize.solwave.domain.model.TransactionPayload
import com.saganize.solwave.domain.model.TransactionRespons
import com.saganize.solwave.domain.model.WalletEntity
import com.saganize.solwave.domain.repository.ApiRepository
import com.saganize.solwave.domain.repository.DataStoreRepository
import com.saganize.solwave.domain.repository.DatabaseRepository
import com.saganize.solwave.presentation.components.SOL_PRIVET_KEY
import com.saganize.solwave.presentation.components.generatePaymentDeepLink
import com.saganize.solwave.presentation.components.getLatestBlockhash
import com.saganize.solwave.ui.viewmodel.events.SolwaveAuthNavEvents
import com.saganize.solwave.ui.viewmodel.events.SolwaveEvents
import com.saganize.solwave.ui.viewmodel.events.SolwaveNavEvents
import com.saganize.solwave.ui.viewmodel.model.Screens
import com.saganize.solwave.ui.viewmodel.model.WalletInfo
import com.saganize.solwave.ui.viewmodel.model.WalletProvider
import com.saganize.solwave.ui.viewmodel.state.SolwaveState
import com.solana.Solana
import com.solana.api.getBalance
import com.solana.api.getSignatureStatuses
import com.solana.core.Transaction
import com.solana.models.SignatureStatusRequestConfiguration
import com.solana.networking.HttpNetworkingRouter
import com.solana.networking.RPCEndpoint
import com.solana.programs.SystemProgram
import com.solana.vendor.TweetNaclFast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.bitcoinj.core.Base58
import java.nio.ByteBuffer
import java.nio.ByteOrder


class SolwaveViewModel(
    private val databaseRepository: DatabaseRepository,
    private val apiRepository: ApiRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val start: String,
    private val transaction: Transaction,
    private val apiKey: String,
    private val context: Context
) : ViewModel() {

    private val _state = mutableStateOf(
        SolwaveState(
            transactionParams = TransactionParams(
                data = TransactionPayload(
                    transaction = transaction
                )
            )
        )
    )
    val state: State<SolwaveState> = _state
    private var solana: Solana = Solana(HttpNetworkingRouter(RPCEndpoint.devnetSolana))

    init {

        if (start == "select") {
            _state.value = state.value.copy(screen = Screens.WallatScreen)
            viewModelScope.launch {
                val selected = dataStoreRepository.getWallet()

                databaseRepository.getWallets().let {
                    it?.find { it.name == selected }?.let { wallet ->
                        _state.value = state.value.copy(wallet = wallet.toWallatInfo())
                    }
                }
            }

        } else {

            Log.d(TAG, transaction.toString())

            viewModelScope.launch {
                val selected = dataStoreRepository.getWallet()
                    ?: throw Exception("You Didn't Select a Wallet.")

                databaseRepository.getWallets().let {
                    it?.find { it.name == selected }?.let { wallet ->
                        navigateToPayScreen(
                            transaction = transaction,
                            currentWallet = wallet.toWallatInfo()
                        )
                    }
                }
            }
        }
    }

    fun onNav(event: SolwaveNavEvents) {
        when (event) {
            SolwaveNavEvents.GoToNoAccountScreen ->
                _state.value =
                    state.value.copy(screen = Screens.NoAccountScreen)

            SolwaveNavEvents.GoToLoginScreen ->
                _state.value =
                    state.value.copy(screen = Screens.LoginScreen)

            SolwaveNavEvents.GoToSignupScreen ->
                _state.value =
                    state.value.copy(screen = Screens.SignupScreen)


            SolwaveNavEvents.GoToPayScreen -> navigateToPayScreen(transaction = transaction)

            SolwaveNavEvents.GoToLoadingScreen ->
                _state.value =
                    state.value.copy(screen = Screens.LoadingScreen)

            SolwaveNavEvents.GoToNoFundsScreen ->
                _state.value =
                    state.value.copy(screen = Screens.NoFundsScreen)

            SolwaveNavEvents.CloseWebViewScreen -> _state.value = state.value.copy(url = null)
            SolwaveNavEvents.GoToWallatScreen -> _state.value =
                state.value.copy(screen = Screens.WallatScreen)

            SolwaveNavEvents.CloseFundsScreen -> {
                _state.value =
                    state.value.copy(error = "No Funds.")
                val activity = context as Activity
                activity.finish()
            }
            SolwaveNavEvents.GoToTransactionDoneScreen -> {}
            SolwaveNavEvents.GoToTransactionErrorScreen -> {}
            SolwaveNavEvents.GoToTransactionFailedScreen -> {}
        }
    }

    fun onAuthEvent(event: SolwaveAuthNavEvents) {
        when (event) {
            is SolwaveAuthNavEvents.InitCreateUser -> {
                event.user?.let { user ->
                    viewModelScope.launch(Dispatchers.Main) {
                        user.getIdToken(true).await().token?.let { token ->
                            withContext(Dispatchers.IO) {
                                apiRepository.initiateCreateUser(
                                    InitiateAuthRequest(
                                        email = user.email!!,
                                        verifyToken = token,
                                    ),
                                ).ifSuccess { response ->

                                    if (response?.errors?.isNotEmpty() == true) {
                                        Log.e(TAG, "Error: ${response.errors.first().message}")
                                        throw Exception(response.errors.first().message)
                                    }

                                    response?.data?.firstOrNull()?.let {
                                        _state.value = state.value.copy(
                                            url = it.url + "?access-token=" + it.accessToken + "&api-key=" + apiKey,
                                        )
                                    }
                                }.ifError {
                                    signOutOfFirebase()
                                    onEvent(SolwaveEvents.WalletError("Something went wrong, please try again later."))
                                    Log.e(TAG, "Error: $it")
                                }
                            }
                        }
                    }
                } ?: {
                    Log.e(TAG, "No authenticated user")
                    throw Exception("Your session has expired. Please login again.")
                }
            }

            is SolwaveAuthNavEvents.InitiateLogin -> {
                event.user?.let { user ->
                    viewModelScope.launch(Dispatchers.Main) {
                        user.getIdToken(true).await().token?.let { token ->

                            withContext(Dispatchers.IO) {
                                apiRepository.initiateLogin(
                                    InitiateAuthRequest(
                                        email = user.email!!,
                                        verifyToken = token,
                                    ),
                                ).ifSuccess { response ->
                                    if (response?.errors?.isNotEmpty() == true) {
                                        Log.e(TAG, "Error: ${response.errors.first().message}")
                                        throw Exception(response.errors.first().message)
                                    }
                                    response?.data?.firstOrNull()?.let {
                                        _state.value = state.value.copy(
                                            url = it.url + "?access-token=" + it.accessToken +
                                                    "&api-key=" + apiKey + "&email=" + user.email,
                                        )
                                    }
                                }.ifError {
                                    signOutOfFirebase()
                                    viewModelScope.launch(Dispatchers.Main){
                                        showToast(context, "This user doesn't exist")
                                    }
                                }
                            }
                        }
                    }
                } ?: {
                    Log.e(TAG, "No authenticated user")
                    throw Exception("Your session has expired. Please login again.")
                }
            }

            is SolwaveAuthNavEvents.InitiateTransaction -> {
                viewModelScope.launch(Dispatchers.Main) {
                    withContext(Dispatchers.IO) {
                        _state.value.wallet?.let { currentWallet ->
                            apiRepository.initiateTransaction(
                                InitiateTransactionRequest(
                                    publicKey = currentWallet.key.toString(),
                                ),
                            ).ifSuccess { response ->
                                if (response?.errors?.isNotEmpty() == true) {
                                    Log.e(TAG, "Error: ${response.errors.first().message}")
                                    throw Exception(response.errors.first().message)
                                }
                                response?.data?.firstOrNull()?.let {
                                    _state.value = state.value.copy(
                                        url = it.url + "?access-token=" + it.authToken + "&api-key=" + apiKey,
                                    )
                                }
                            }.ifError {
                                onEvent(SolwaveEvents.TransactionFailed("Something went wrong, please try again later."))
                                signOutOfFirebase()
                                Log.e(TAG, "Error: $it")
//                                    throw Exception(it)
                            }
                        }
                    }
                }
            }

            is SolwaveAuthNavEvents.OnCreateUserDone -> {

                event.key?.let{
                    onEvent(SolwaveEvents.SaveWallet(WalletProvider.Saganize, it))
                }
                /*
                if (!event.key.isNullOrEmpty()) {
                    val wallet = WalletEntity(
                        WalletProvider.Saganize.toName(),
                        event.key,
                    )
                    viewModelScope.launch {
                        saveWallet(wallet)
                    }
                }*/
            }

            is SolwaveAuthNavEvents.OnLoginDone -> {
                event.key?.let{
                    onEvent(SolwaveEvents.SaveWallet(WalletProvider.Saganize, it))
                }/*
                if (!event.key.isNullOrEmpty()) {
                    viewModelScope.launch {
                        saveWallet(
                            WalletEntity(
                                WalletProvider.Saganize.toName(),
                                event.key
                            )
                        )
                    }
                }*/
            }

            is SolwaveAuthNavEvents.OnTransactionDone -> {
                // TODO: this
            }
        }
    }

    fun onEvent(event: SolwaveEvents) {
        when (event) {
            is SolwaveEvents.SelectWallat -> {
                viewModelScope.launch {
                    databaseRepository.getWallets()?.find { it.name == event.wallat.toName() }
                        .let {
                            withContext(Dispatchers.Main) {
                                if (it != null) {
                                    _state.value = state.value.copy(wallet = it.toWallatInfo())
                                    saveLastUsedWallet(event.wallat)
                                    val activity = context as Activity
                                    activity.finish()
                                } else {
                                    // open app
                                    when (event.wallat) {
                                        WalletProvider.Saganize -> {
                                            _state.value =
                                                state.value.copy(screen = Screens.NoAccountScreen)
                                        }

                                        else -> event.openDeepLink()
                                    }
                                }
                            }
                        }
                }
            }

            is SolwaveEvents.SaveWalletFromWebview -> {
                viewModelScope.launch {
                    databaseRepository.saveWallet(
                        WalletEntity(
                            name = event.wallat.toName(),
                            key = event.Key
                        )
                    )

                    withContext(Dispatchers.Main) {
                        _state.value = state.value.copy(wallet = WalletInfo(event.wallat, event.Key), screen = Screens.WallatScreen)
                        //navigateToPayScreen(transaction)
                    }
                }
                saveLastUsedWallet(event.wallat)

                val activity = context as Activity
                activity.finish()
            }


            is SolwaveEvents.SaveWallet -> {
                viewModelScope.launch {
                    databaseRepository.saveWallet(
                        WalletEntity(
                            name = event.wallat.toName(),
                            key = event.Key
                        )
                    )

                    withContext(Dispatchers.Main) {
                        _state.value = state.value.copy(wallet = WalletInfo(event.wallat, event.Key), screen = Screens.WallatScreen)
                        navigateToPayScreen(transaction)
                    }
                }
                saveLastUsedWallet(event.wallat)
                val activity = context as Activity
                activity.finish()
            }

            is SolwaveEvents.PayUsingWallet -> {

                runBlocking(Dispatchers.IO) {
                    solana.api.getLatestBlockhash().getOrNull()
                }?.let { bh ->

                    val uri = state.value.wallet
                    val keys = uri!!.key.split("/")

                    val deeplink = generatePaymentDeepLink(
                        keys[0],
                        keys[1],
                        keys[2],
                        bh,
                        state.value.wallet!!.walletProvider
                    )

                    _state.value = state.value.copy(deepLink = deeplink)
                    event.openDL()

                }

            }

            is SolwaveEvents.WalletError -> {
                _state.value =
                    state.value.copy(error = event.error, screen = Screens.TransactionErrorScreen)
            }

            is SolwaveEvents.TransactionFailed -> {
                _state.value =
                    state.value.copy(error = event.error, screen = Screens.TransactionFailedScreen)
            }

            is SolwaveEvents.DecryptTransactionResult -> {
                val data = event.data
                val nonce = event.nonce

                runBlocking(Dispatchers.IO) {
                    databaseRepository.getWallets()
                        ?.find { it.name == state.value.wallet!!.walletProvider.toName() }
                        ?.key?.split("/")
                        ?.get(2)?.run {

                            val _privateKeyBytes = Base58.decode(SOL_PRIVET_KEY)
                            val _nonceBytes = Base58.decode(nonce)
                            val _dataBytes = Base58.decode(data)
                            val _phantomKeyBytes = Base58.decode(this)


                            val box = TweetNaclFast.Box(_phantomKeyBytes, _privateKeyBytes)
                            val decryptedDataBytes = box.open(_dataBytes, _nonceBytes)
                            val jsonData = String(decryptedDataBytes, Charsets.UTF_8)

                            Gson().fromJson(jsonData, TransactionRespons::class.java)
                        }
                }?.let {
                    _state.value = state.value.copy(screen = Screens.LoadingScreen)
                    checkTransactionStatus(it.signature)
                }
            }

            SolwaveEvents.NoNet -> {
                _state.value = state.value.copy(
                    error = "There is No Internet Connection.",
                    screen = Screens.TransactionErrorScreen
                )
            }

            is SolwaveEvents.TransactionDone -> {
                _state.value = state.value.copy(screen = Screens.LoadingScreen)
                checkTransactionStatus(event.id)
            }
        }
    }

    private fun navigateToPayScreen(
        transaction: Transaction,
        currentWallet: WalletInfo? = null,
        walletList: List<WalletInfo>? = null,
    ) {
        val wallet = currentWallet ?: state.value.wallet ?: return

        // TODO: takes a lot time to load


        viewModelScope.launch {
            Log.d(TAG, "Simulating transaction... ${transaction.stringify()}")
            apiRepository.simulateTransaction(
                SimulateTransactionRequest(
                    transaction = transaction.stringify(),
                    publicKey = wallet.key.split("/").get(0),
                ),
            ).ifSuccess { response ->
                if (response?.errors?.isNotEmpty() == true) {
                    Log.e(TAG, "Error: ${response.errors.first().message}")
                    throw Exception(response.errors.first().message)
                }

                Log.d(TAG, "Response: $response")

                response?.data?.firstOrNull()?.let {
                    val tsxStatus = when (it.status) {
                        "success" -> TransactionStatus.SUCCESS
                        "failed" -> TransactionStatus.FAILED
                        else -> throw IllegalArgumentException()
                    }

                    val simulationType = getTransactionType()

                    Log.d(TAG, it.toString())

                    val transactionPayload = _state.value.transactionParams.data.copy(
                        transaction = transaction,
                        lamports = it.amount?.toDouble(),
                        fees = it.networkFee,
                    )

                    _state.update {
                        copy(
                            transactionParams = _state.value.transactionParams.copy(
                                type = simulationType,
                                status = tsxStatus,
                                data = transactionPayload,
                            ),
                            screen = Screens.PayScreen(
                                transactionParams = TransactionParams(
                                    type = simulationType,
                                    status = tsxStatus,
                                    data = TransactionPayload(
                                        transaction = transaction,
                                        fees = it.networkFee,
                                        lamports = it.amount?.toDouble(),
                                        to = it.toAddress
                                    )
                                )
                            ),
                            url = null,
                            wallet = wallet,
                        )
                    }
                }
            }.ifError {
                Log.e(TAG, "Error: $it")
                signOutOfFirebase()
                throw Exception(it)
            }
        }

    }
    private fun saveLastUsedWallet(wallet: WalletProvider) {
        viewModelScope.launch(Dispatchers.Main) {
            dataStoreRepository.saveWallet(wallet.toName())
        }
    }

    fun saveWallet(wallet: WalletEntity) {
        viewModelScope.launch {
            onEvent(
                SolwaveEvents.SaveWallet(
                    WalletProvider.Saganize,
                    wallet.key
                )
            )
        }
    }

    private fun getTransactionType(): SimulationType {
        transaction.instructions.forEach { instruction ->

            if (instruction.programId == SystemProgram.PROGRAM_ID) {
                val fromAddress = instruction.keys[0].publicKey

                val toAddress = instruction.keys[1].publicKey

                val lamports = ByteBuffer
                    .wrap(instruction.data, 4, 8)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .long

                val transactionParams = _state.value.transactionParams.copy(
                    data = _state.value.transactionParams.data.copy(
                        from = fromAddress.toString(),
                        to = toAddress.toString(),
                        lamports = lamports.toDouble(),
                    )
                )
                _state.update {
                    copy(
                        transactionParams = transactionParams
                    )
                }
                return SimulationType.TRANSFER
            }
        }
        return SimulationType.OTHER
    }



    private fun checkTransactionStatus(transactionId: String) = viewModelScope.launch {
        solana.let { solana ->
            withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
                val list = listOf(transactionId)
                val config = SignatureStatusRequestConfiguration(
                    false
                )

                // TODO: repeat only 10 times
                var tryes = 0

                var status = "null"
                while (status != "finalized" && tryes<10) {
                    tryes ++
                    try {
                        val signature = solana.api.getSignatureStatuses(list, config).getOrThrow()
                        status = signature[0].confirmationStatus.toString()
                        Log.d(
                            TAG,
                            "Transaction status: $signature",
                        )
                    } catch (e: Exception) {
                        Log.d(
                            TAG,
                            "Transaction status: null",
                        )
                    }

                    delay(2000)
                }

                if (tryes>10){
                    _state.value = state.value.copy(
                        error = "Somthing Went Wrong.",
                        screen = Screens.TransactionFailedScreen
                    )
                }else{
                    _state.value = state.value.copy(
                        screen = Screens.TransactionDoneScreen,
                        transactionId = transactionId
                    )
                }

            }
        }
    }

    fun hasFunds() {

        val keys = state.value.wallet!!.key.split("/")
        val publickKey = keys[0]

        solana.api.getBalance(com.solana.core.PublicKey(publickKey)) {
            it.getOrNull()?.run {
                Log.d(TAG, this.toString())
                _state.value = state.value.copy(funds = this)
            }
        }
    }

    fun signOutOfFirebase(){
        val googleSignInClient = GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN)
        val firebaseAuth = FirebaseAuth.getInstance()

        googleSignInClient.signOut().addOnCompleteListener {
            firebaseAuth.signOut()
        }
    }

    override fun onCleared() {
        if (start == "select") {
            if (state.value.screen == Screens.WallatScreen) {
                val key = if(state.value.wallet?.walletProvider == WalletProvider.Saganize)state.value.wallet?.key else state.value.wallet?.key?.split("/")?.get(0) ?: ""
                onSelect(SelectResult.Success(key ?: ""))
            } else {
                onSelect(SelectResult.Error(state.value.error))
            }

        } else {
            if (state.value.screen == Screens.TransactionDoneScreen) {
                onComplete(com.saganize.solwave.core.models.TransactionResult.Success(state.value.transactionId))
            } else {
                onComplete(com.saganize.solwave.core.models.TransactionResult.Error(state.value.error))
            }
        }

        signOutOfFirebase()

        super.onCleared()
    }
}