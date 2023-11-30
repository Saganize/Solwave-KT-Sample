package com.example.saganizetest

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.saganizetest.ui.theme.SaganizeTestTheme
import com.saganize.solwave.Solwave
import com.solana.core.PublicKey
import com.solana.core.Transaction
import com.solana.programs.SystemProgram

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val datastore = DataStoreRepository(this)

        setContent {
            SaganizeTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val context = LocalContext.current
                    val solwave = Solwave(context, apiKey = API_KEY)

                    var publicKey by remember {
                        mutableStateOf("")
                    }

                    publicKey = datastore.getPublicKey()

                    val solTransferInstruction = SystemProgram.transfer(
                        PublicKey(publicKey),
                        PublicKey("Bu3mTU2X7SoZUkyNU37jispVqRLkSSwiQuN6rGbvQx9f"),
                        10000L,
                    )

                    val transaction = Transaction().addInstruction(solTransferInstruction)

                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                solwave.selectWallet(
                                    onSuccess = { walletKey ->
                                        Log.d(TAG, walletKey)

                                        // Saving the PublicKey
                                        datastore.savePublicKey(walletKey)
                                        publicKey = walletKey
                                    },
                                    onFailure = { error ->
                                        Log.d(TAG, error.message)
                                    }
                                )
                            }
                        ) {
                            Text(text = "Select Wallet")
                        }
                        Spacer(modifier = Modifier.size(40.dp))

                        Button(
                            onClick = {
                                solwave.performTransaction(
                                    transaction = transaction,
                                    onSuccess = { transactionId ->
                                        Log.d(TAG, transactionId)
                                    },
                                    onFailure = { error ->
                                        Log.d(TAG, error.message)
                                    }
                                )
                            },
                            enabled = publicKey.isNotBlank()
                        ) {
                            Text(text = "Start payment")
                        }



                    }
                }
            }
        }
    }
}