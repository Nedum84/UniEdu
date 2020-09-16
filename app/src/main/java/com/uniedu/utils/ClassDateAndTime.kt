package com.uniedu.utils

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

class ClassDateAndTime {

    fun dateTimePast(timeStamp:Long):String{
// it comes out like this 2013-08-31 15:55:22 so adjust the date format
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = df.parse(getDateTimeWithMinsHrs(timeStamp))
        val epoch = date.time
        return DateUtils.getRelativeTimeSpanString(epoch, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString()//3 mins ago
    }


    private fun getDateTimeWithMinsHrs(timeStamp: Long): String? {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())//Locale.US for usa
            val netDate = Date(timeStamp*1000)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    fun getDateTime(s: Long = System.currentTimeMillis()): String {//current time in millisec
        try {
            val sdf = SimpleDateFormat("hh:mm a  EEE. MMM dd, yyyy", Locale.getDefault())//2:23 pm |h->5:23am, hh->05:40pm
            val netDate = Date(s)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }
    fun getDateTime2(s: Long): String? {
        try {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())//Wed, Mar 21, 2019
            val netDate = Date(s*1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }
    fun checkDateTimeFirst(s:Long):String{
        val currentTime = (System.currentTimeMillis()/1000)
        return if ((currentTime - s)<345600){//4 days ago
//            dateTimePast(s)//3 minutes ago
            findReplace(dateTimePast(s))//3 mins ago
        }else{
            getDateTime2(s)!!//Mar 21, 2019
        }
    }
    private fun findReplace(str:String):String{
        return str.replace("hour","hr")
                .replace("minute","min")
                .replace("second","sec")
    }
}