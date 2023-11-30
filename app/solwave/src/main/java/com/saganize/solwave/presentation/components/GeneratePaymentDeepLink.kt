package com.saganize.solwave.presentation.components

import com.google.gson.Gson
import com.iwebpp.crypto.TweetNacl
import com.saganize.solwave.ui.viewmodel.model.WalletProvider
import com.saganize.solwave.domain.model.SolWallatKey
import com.solana.core.PublicKey
import com.solana.core.SerializeConfig
import com.solana.core.Transaction
import com.solana.programs.SystemProgram
import com.solana.vendor.TweetNaclFast
import org.bitcoinj.core.Base58
import org.json.JSONObject


fun generatePaymentDeepLink(
    from: String,
    session: String,
    sharedSecret: String,
    blockhash: String,
    walletProvider: WalletProvider,
    amount: Double = 10.0
): String {

    val solTransferInstruction = SystemProgram.transfer(
        PublicKey(from),
        PublicKey("AhAnmLBjpZmzzXmn8QSD4yc9YEtiHcziF35aKvNo7Uuv"),
        100L,
    )
    val transaction = Transaction().apply {
        addInstruction(solTransferInstruction)
        setRecentBlockHash(blockhash)
        feePayer = PublicKey(from)
    }

    val serializedTransaction = Base58.encode(
        transaction.serialize(
            SerializeConfig(
                requireAllSignatures = false,
                verifySignatures = false
            )
        )
    )

    val payloadMap = mapOf(
        "transaction" to serializedTransaction,
        "session" to session
    )



    // encreption stuff

    val nonce = TweetNaclFast.randombytes(24)
    val pj = JSONObject(payloadMap).toString().toByteArray()
    val nn = nonce
    val ss = Base58.decode(sharedSecret)


    val box = TweetNacl.Box(ss, Base58.decode(SOL_PRIVET_KEY)).after(pj, nn)

    val encryptedPayload = Base58.encode(box)

    val dpInit =
        if (walletProvider == WalletProvider.Phantom) "https://phantom.app/ul/v1/signAndSendTransaction?" else "https://solflare.com/ul/v1/signAndSendTransaction?"

    val deeplink = dpInit +
            "&dapp_encryption_public_key=$SOL_PUBLIC_KEY" +
            "&nonce=${Base58.encode(nonce)}" +
            "&redirect_link=app%3A%2F%2Fsolwave.com%2Fdeeplink" +
            "&payload=$encryptedPayload"

    return deeplink
}

fun decryptData(wallatKey: String, privateKey: String, nonce: String, data: String): String {
    val _privateKeyBytes = Base58.decode(privateKey)
    val _phantomKeyBytes = Base58.decode(wallatKey)
    val _nonceBytes = Base58.decode(nonce)
    val _dataBytes = Base58.decode(data)

    val box = TweetNaclFast.Box(_phantomKeyBytes, _privateKeyBytes)
    val decryptedDataBytes = box.open(_dataBytes, _nonceBytes)
    val jsonData = String(decryptedDataBytes, Charsets.UTF_8)

    val solWallatKey = Gson().fromJson(jsonData, SolWallatKey::class.java)

    return solWallatKey.public_key+"/"+solWallatKey.session+"/"+wallatKey
}