package com.saganize.solwave.presentation.components

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.saganize.solwave.core.di.TAG


const val SOL_PUBLIC_KEY = "J1gY1vugvhFJQfDrdJsWxr17E8S87Lr3nTxy1ojk7wdH"
const val SOL_PRIVET_KEY = "9dqcCWwTRhWhM68QYU7SMD735iTn2361i761wdYdvBTX"


@Composable
fun SolWallatesLauncher(deeplink: String): () -> Unit {

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deeplink))

    val activityResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        Log.d(TAG, "result and stuff "+result.resultCode.toString())

    }



    if (deeplink.isNotEmpty()){

        Log.d(TAG, "result and stuff $deeplink")

        return { activityResultLauncher.launch(intent) }
    }

    return {}
}
