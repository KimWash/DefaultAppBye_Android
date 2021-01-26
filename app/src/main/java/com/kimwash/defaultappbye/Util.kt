package com.kimwash.defaultappbye

import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate

object Util {
    fun isNightModeActive(context: Context): Boolean {
        val defaultNightMode = AppCompatDelegate.getDefaultNightMode()
        if (defaultNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            return true
        }
        if (defaultNightMode == AppCompatDelegate.MODE_NIGHT_NO) {
            return false
        }
        val currentNightMode = (context.resources.configuration.uiMode
                and Configuration.UI_MODE_NIGHT_MASK)
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> return false
            Configuration.UI_MODE_NIGHT_YES -> return true
            Configuration.UI_MODE_NIGHT_UNDEFINED -> return false
        }
        return false
    }
    fun dispDialog(context: Context, msg:String, okEvent: () -> Unit, title:String, icon:Int?){
        val alert_confirm = AlertDialog.Builder(context)
        alert_confirm.setMessage(msg)
        alert_confirm.setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
            okEvent()
        })
        if (icon != null){
            alert_confirm
                .setTitle(title)
                .create()
                .setIcon(icon)
        }
        else{
            alert_confirm
                .setTitle(title)
                .create()
        }
        alert_confirm.show()
    }

}