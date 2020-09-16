package com.uniedu.extension

import android.content.Context
import android.widget.EditText
import android.widget.Toast

fun Context.toast(message:String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
fun String.toString2(input:EditText?)  = input.toString().trim()