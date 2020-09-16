package com.uniedu.network

import com.google.gson.annotations.SerializedName
import com.uniedu.model.MyDetails
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface LoginRegService {
    @Multipart
    @POST("login.php")
    fun loginRequest(
        @Part("request_type") request_type:String,
        @Part("email_mobile_no") email_mobile_no:String,
        @Part("password") password:String
    ): Call<ServerResponse>


    @Multipart
    @POST("register_user.php")
    fun registerRequest(
        @Part("request_type") request_type:String,
        @Part("name") name:String,
        @Part("mobile_no") mobile_no:String,
        @Part("email") email:String,
        @Part("password") password:String
    ): Call<ServerResponse>

}
