package com.uniedu.network

import com.google.gson.annotations.SerializedName
import com.uniedu.UrlHolder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ServerResponse {
    // variable name should be same as in the json response from php
    @SerializedName("success")
    val success:Boolean? = null
    @SerializedName("resp_message")
    val respMessage: String? = null
    @SerializedName("other_detail")
    val otherDetail: String? = null
}


class RetrofitPOST {

    companion object{
        private val client: OkHttpClient =  OkHttpClient.Builder()
            .connectTimeout(4, TimeUnit.SECONDS)//timeout after n(4) econds
            .readTimeout(4, TimeUnit.SECONDS)
            .build()

        //    Retrofit with json response
        val retrofitWithJsonResponse: Retrofit = Retrofit.Builder()
            .baseUrl(UrlHolder.URL_ROOT)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}