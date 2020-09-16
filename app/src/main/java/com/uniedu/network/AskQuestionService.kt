package com.uniedu.network

import com.google.gson.annotations.SerializedName
import com.uniedu.model.MyDetails
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface AskQuestionService {
    @Multipart
    @POST("ask_question.php")
    fun rapeComplainRequest(
//        @Header("Authorization") authorization: String ,
        @Part("request_type") request_type: String,
        @Part("my_details") my_details: MyDetails,
        @Part("question_body") question_body: String,
        @PartMap imgMap: Map<String, RequestBody>? = null,
        @Part("course") course: String
    ): Call<ServerResponse>
}

