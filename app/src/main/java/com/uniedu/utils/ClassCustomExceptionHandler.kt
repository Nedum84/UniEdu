package com.uniedu.utils

import android.os.Environment
import java.io.*
import java.util.*


class ClassCustomExceptionHandler(private val localPath: String?, private val url: String?): Thread.UncaughtExceptionHandler {/*
 * if any of the parameters is null, the respective functionality
 * will not be used
 */

    private val defaultUEH= Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(t:Thread, e:Throwable) {
        val timestamp = Calendar.getInstance().timeInMillis.toString()+"_${(100..999).shuffled().last()}"
        val result = StringWriter()
        val printWriter = PrintWriter(result)
        e.printStackTrace(printWriter)
        val stacktrace = result.toString()
        printWriter.close()
        val filename = "$timestamp.txt"
        if (localPath != null){
            if (Environment.getExternalStorageDirectory().canWrite()){
                val dir = File(localPath)
                if (!dir.exists()) {
                    dir.mkdirs()
                }

                writeToFile(stacktrace, filename)
            }
        }
        if (url != null){
//            sendToServer(stacktrace, filename)
        }
        defaultUEH?.uncaughtException(t, e)
    }
    private fun writeToFile(stacktrace:String, filename:String) {
        try{
            val bos = BufferedWriter(FileWriter("$localPath/$filename"))
            bos.write(stacktrace)
            bos.flush()
            bos.close()
        }
        catch (e:Exception) {
            e.printStackTrace()
        }
    }
//    private fun sendToServer(stacktrace:String, filename:String) {
////        val httpClient = DefaultHttpClient()
////        val httpPost = HttpPost(url)
////        val nvps = ArrayList<NameValuePair>()
////        nvps.add(BasicNameValuePair("filename", filename))
////        nvps.add(BasicNameValuePair("stacktrace", stacktrace))
////        try
////        {
////            httpPost.setEntity(
////                    UrlEncodedFormEntity(nvps, HTTP.UTF_8))
////            httpClient.execute(httpPost)
////        }
////        catch (e:IOException) {
////            e.printStackTrace()
////        }
//
//
//        //creating volley string request
//        val stringRequest = object : StringRequest(Request.Method.POST, UrlHolder.URL_APP_CRASH_REPORT_UPLOAD,
//                Response.Listener<String> { response ->
//
//                    try {
////                        val obj = JSONObject(response)
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                },
//                Response.ErrorListener { _ ->
//                    //                    ClassAlertDialog(context).toast("ERROR IN NETWORK CONNECTIONnbb!")
//                }) {
//            @Throws(AuthFailureError::class)
//            override fun getParams(): Map<String, String> {
//                val params = HashMap<String, String>()
//                params["request_type"] = "send_crash_report"
//                params["filename"] = filename
//                params["stacktrace"] = stacktrace
//                return params
//            }
//        }
//        //adding request to queue
//        VolleySingleton.instance?.addToRequestQueue(stringRequest)
//        //volley interactions end
//    }
}