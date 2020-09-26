package com.uniedu.network

import com.uniedu.model.MyDetails
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface AnswerQuestionService {
    @Multipart
    @POST("answer_question.php")
    fun rapeComplainRequest(
        @Header("Authorization") authorization: String ,
        @Part("request_type") request_type: String,
        @Part("my_details") my_details: MyDetails,
        @Part("answer_body") answer_body: String,
        @PartMap imgMap: Map<String, RequestBody>,
        @Part("question_id") question_id: String
    ): Call<ServerResponse>
}

