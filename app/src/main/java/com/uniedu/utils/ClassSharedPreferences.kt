package com.uniedu.utils

import android.content.Context
import com.google.gson.Gson
import com.uniedu.UrlHolder
import com.uniedu.model.MyDetails

class  ClassSharedPreferences(val context: Context?){

    private val PREFERENCE_NAME = "uniedu_preference"
    private val PREFERENCE_USER_DETAIL = "cur_user_detail"
    private val PREFERENCE_ACCESS_LEVEL= "access_level"
    private val PREFERENCE_OPENING_FOR_THE_FIRST_TIME= "opening_for_the_first_time"
    private val PREFERENCE_CUR_IMG_UPLOAD_PATH = "cur_image_upload_path"


    private val preference = context?.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)!!


    //set Current User Detail
    fun setCurUserDetail(data:String){
        val editor = preference.edit()
        editor.putString(PREFERENCE_USER_DETAIL,data)
        editor.apply()
    }
    fun getCurUserDetail():MyDetails{
        val stringDetails = preference.getString(PREFERENCE_USER_DETAIL, "")!!
        val myDetails = Gson().fromJson(stringDetails, MyDetails::class.java);
        return  myDetails
    }
    //set access level
    fun setAccessLevel(data:Int){
        val editor = preference.edit()
        editor.putInt(PREFERENCE_ACCESS_LEVEL,data)
        editor.apply()
    }
    fun getAccessLevel() = preference.getInt(PREFERENCE_ACCESS_LEVEL,0)


    //set first time app opening
    fun setOpeningForTheFirstTime(id:Boolean){
        val editor = preference.edit()
        editor.putBoolean(PREFERENCE_OPENING_FOR_THE_FIRST_TIME,id)
        editor.apply()
    }
    //get first time app opening
    fun getOpeningForTheFirstTime():Boolean{
        return  preference.getBoolean(PREFERENCE_OPENING_FOR_THE_FIRST_TIME,true)
    }

    //set img upload url for cropping
    fun setImgUploadPath(url:String){
        val editor = preference.edit()
        editor.putString(PREFERENCE_CUR_IMG_UPLOAD_PATH,url)
        editor.apply()
    }
    fun getImgUploadPath():String{
        return  preference.getString(PREFERENCE_CUR_IMG_UPLOAD_PATH,"")!!
    }





    fun isLoggedIn():Boolean{
        return getCurUserDetail().name.isNotEmpty()
    }


    fun logoutUser() : Boolean{
        val userEditor = preference.edit()
        userEditor.clear()
        userEditor.apply()

        return true
    }
}