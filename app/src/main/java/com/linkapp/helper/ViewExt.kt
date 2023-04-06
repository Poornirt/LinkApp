package com.linkapp.helper

import android.content.Context
import android.widget.Toast

object ViewExt {

    fun Context.showToast(msg:String) {
        Toast.makeText(
            this,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }

}