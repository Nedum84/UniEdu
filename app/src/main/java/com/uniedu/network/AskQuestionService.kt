package com.uniedu.network


import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface AskQuestionService {
    @Multipart
    @POST("add_question.php")
    fun askQuestionRequest(
//        @Header("Authorization") authorization: String ,
        @Part("request_type") request_type: String,
        @Part("question_from") question_from: Int,
        @Part("school_id") school_id: String,
        @Part("question_body") question_body: String,
        @PartMap imgMap: Map<String, @JvmSuppressWildcards RequestBody>? = null,
        @Part("course") course: String,
        @Part("is_adding_new_question") is_adding_new_question:Boolean, //to know if you are adding or editing(false)
        @Part("image_is_removed") image_is_removed:Boolean = false
    ): Call<ServerResponse>
}

interface UploadImage{

    @Multipart
    @POST("img_upload.php")
    fun upload(
        @Part("request_type") request_type:String,
        @Part("image_type") image_type:String,
        @PartMap map: Map<String, @JvmSuppressWildcards RequestBody>? = null
    ):Call<ServerResponse>
}




