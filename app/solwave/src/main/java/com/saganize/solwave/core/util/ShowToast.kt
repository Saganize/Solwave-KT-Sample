package com.saganize.solwave.core.util

import android.content.Context
import android.widget.Toast

fun showToast(context: Context, it: String){
    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
}