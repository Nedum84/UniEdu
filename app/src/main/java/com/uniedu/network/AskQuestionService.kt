package com.uniedu.network

import com.google.gson.annotations.SerializedName
import com.uniedu.model.MyDetails
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface AskQuestionService {
    @Multipart
    @POST("ask_question.php")
    fun askQuestionRequest(
//        @Header("Authorization") authorization: String ,
        @Part("request_type") request_type: String,
        @Part("question_uploader") question_uploader: Int,
        @Part("question_body") question_body: String,
        @PartMap imgMap: Map<String, @JvmSuppressWildcards RequestBody>? = null,
        @Part("course") course: String,
        @Part("is_adding_new_question") is_adding_new_question:Boolean, //to know if you are adding or editing(false)
        @Part("image_is_removed") image_is_removed:Boolean = false
    ): Call<ServerResponse>
}

