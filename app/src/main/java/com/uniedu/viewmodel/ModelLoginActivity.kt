package com.uniedu.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.google.gson.Gson
import com.uniedu.Event
import com.uniedu.model.*
import com.uniedu.room.DatabaseRoom
import com.uniedu.utils.ClassSharedPreferences
import org.json.JSONObject


class ModelLoginActivity(val app: Application) : AndroidViewModel(app) {

    private val database = DatabaseRoom.getDatabaseInstance(app)


    val loginSuccess: LiveData<Event<Boolean>> get() = _loginSuccess
    private val _loginSuccess = MutableLiveData<Event<Boolean>>()
    fun setLoginSuccess(data: Boolean) {
        _loginSuccess.value = Event(data)
    }







    //saving user's details
    fun saveUserDetails(other_detail: JSONObject?) {
        try {
            val prefs = ClassSharedPreferences(app)
            val details = other_detail!!.getJSONArray("userDetails").getJSONObject(0)

            val user = MyDetails(
                details.getInt("user_id"),
                details.getString("name"),
                details.getString("mobile_no"),
                details.getString("email"),
                details.getString("photo"),
                details.getString("reg_date"),
                details.getString("user_school"),
                details.getString("user_level")
            )

            prefs.setCurUserDetail(Gson().toJson(user))

            prefs.setAccessLevel(user.user_level.toInt())
        } catch (e: Exception) {
            e.printStackTrace()
        }



        setLoginSuccess(true)
    }











    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ModelLoginActivity::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ModelLoginActivity(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}