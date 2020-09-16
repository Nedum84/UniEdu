package com.uniedu.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.Toast
import android.app.AlertDialog
import com.google.android.material.snackbar.Snackbar


class ClassAlertDialog(var context:Context) {

    init {
        val alertDialog: AlertDialog?=null
    }

    fun updateAppVersionNotStrict(show_msg:String){
        AlertDialog.Builder(context)
                .setTitle("Update needed!")
                .setMessage(show_msg)
                .setPositiveButton("Update"
                ) { _, _ ->
                    redirectToPlayStore()
                }
                .setNegativeButton("Not Now"
                ) { dialog, id ->
                    //                     alertDialog.d
                }.setCancelable(false)
                .show()
    }
    fun updateAppVersionStrict(show_msg:String){
        AlertDialog.Builder(context)
                .setTitle("Update needed!")
                .setMessage(show_msg)
                .setPositiveButton("Update"
                ) { _, _ ->
                    redirectToPlayStore()
                }
                .setNegativeButton("Close"
                ) { dialog, id ->
                    (context as Activity).finish()
                }.setCancelable(false)
                .show()
    }


    fun redirectToPlayStore(){
        val appPackageName = context.packageName // getPackageName() from Context or Activity object
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ContextCompat.startActivity(context, intent, Bundle())
        } catch (anfe: android.content.ActivityNotFoundException) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName"))
            val chooser = Intent.createChooser(intent,"Update using:")
            ContextCompat.startActivity(context, chooser, Bundle())
        }
    }


    fun alertMessage(displayMsg:String,title:String=""){
        if(title=="")
            AlertDialog.Builder(context)
                .setMessage(displayMsg)
                .setPositiveButton("Ok"
                ) { _, id ->
                }.setCancelable(false)
                .show()
        else
            AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(displayMsg)
                    .setPositiveButton("Ok"
                    ) { _, id ->
                    }.setCancelable(false)
                    .show()
    }

    fun snackBarMsg(view: View, msg :String= "No Internet Connection"){
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).setAction("Action", null).show()
    }

    fun toast(msg:String, duration: Int = Toast.LENGTH_LONG){
        Toast.makeText(context, msg, duration).show()
    }


}