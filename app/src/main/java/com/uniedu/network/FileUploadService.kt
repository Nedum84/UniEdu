package com.uniedu.network


import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface UploadEBookService {
    @Multipart
    @POST("add_ebook.php")
    fun upload(
        @Part("request_type") request_type: String,
        @Part("book_id") book_id: Int? = null,
        @Part("book_uploaded_by") book_uploaded_by: Int,
        @Part("book_title") book_title: String,
        @Part("book_desc") book_desc: String,
        @Part("book_no_of_pages") book_no_of_pages: Int? = 0,
        @PartMap fileMap: Map<String, @JvmSuppressWildcards RequestBody>? = null,
        @Part("book_type") book_type: String,//pdf or image
        @Part("course_id") course_id: String,
        @Part("school_id") school_id: String,
        @Part("is_adding_new") is_adding_new:Boolean //to know if you are adding or editing(false)
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




