package com.saganize.solwave.core.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.MutableState
import com.google.gson.GsonBuilder
import com.saganize.solwave.core.util.Constants.LAMPORTS_PER_SOL
import com.saganize.solwave.domain.model.TransactionSerializer
import com.solana.core.PublicKey
import com.solana.core.Transaction

fun Context.isAppInstalled(packageName: String): Boolean {
    val packageManager: PackageManager = packageManager
    return try {
        val packageInfo = packageManager.getApplicationInfo(packageName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}


fun String.toPublicKey(): PublicKey {
    return PublicKey(this)
}

fun PublicKey.display(): String {
    return try {
        toBase58().substring(0, 4) + "..." + toBase58().substring(toBase58().length - 4)
    }catch (e:Exception){
        ""
    }
}

fun String.displayWallet(): String {
    return substring(0, 4) + "..." + substring(length - 4)
}

fun Double.formatFee(): String {
    return try {
        if (this < 0.0001) "<0.0001" else String.format("%.4f", this)
    } catch (e: Exception) {
        "<0.0001"
    }
}

fun Double.formatLamportsToSol(): String {
    return try {
        val sol = this / LAMPORTS_PER_SOL
        if (sol < 0.0001) "<0.0001" else String.format("%.4f", sol)
    } catch (e: Exception) {
        "<0.0001"
    }
}


fun Transaction.stringify(): String {
    val gsonBuilder = GsonBuilder()
    gsonBuilder.registerTypeAdapter(Transaction::class.java, TransactionSerializer())
    val gson = gsonBuilder.create()
    return gson.toJson(this)
}

fun <T> MutableState<T>.update(update: T.() -> T) {
    value = value.update()
}

internal fun Double.toSol(): String {
    val x = (this/1000000000).toFloat()

    return if (x<= 0.00001) "0.00001 " else x.toString()
}