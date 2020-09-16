package com.ng.rapetracker.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.google.gson.Gson
import com.uniedu.Event
import com.uniedu.room.DatabaseRoom
import com.uniedu.utils.ClassSharedPreferences
import com.uniedu.model.ItemsForSale
import com.uniedu.model.Questions
import com.uniedu.model.SchoolTypes
import com.uniedu.model.Schools
import org.json.JSONObject


class ModelLoginActivity(application: Application) : AndroidViewModel(application) {
    val appCtx = application

    init {

    }
    private val database = DatabaseRoom.getDatabaseInstance(application)
    val allRapeType: LiveData<List<Schools>> = database.rapeTypeDao.getAllRapeType()
    val allRapeTypeOfVictim: LiveData<List<SchoolTypes>> = database.rapeTypeOfVictimDao.getAllRapeOfVictimType()
    val allRapeSupportType  = database.rapeSupportTypeDao.getAllRapeSupport()


    val gotoMainActivity: LiveData<Event<Boolean>> get() = _gotoMainActivity
    private val _gotoMainActivity = MutableLiveData<Event<Boolean>>()
    fun setGotoMainActivity(flag: Boolean) {
        _gotoMainActivity.value = Event(flag)
    }







    //saving user's details
    fun saveUserDetails(other_detail: JSONObject?, prefs: ClassSharedPreferences) {
        try {
            val details = other_detail!!.getJSONArray("userDetails").getJSONObject(0)

            if (details!!.getInt("access_level") == 1){
                val user = ItemsForSale(
                    details.getInt("id"),
                    details.getString("user_name"),
                    details.getString("user_mobile_no"),
                    details.getString("user_email"),
                    details.getInt("user_gender"),
                    details.getInt("user_age"),
                    details.getInt("user_country"),
                    details.getInt("user_state"),
                    details.getString("user_address"),
                    details.getString("user_reg_date"),
                    details.getInt("access_level")
                )
                prefs.setCurUserDetail(Gson().toJson(user))
            }else{
                val org = Questions(
                    details.getInt("id"),
                    details.getString("org_name"),
                    details.getInt("org_type"),
                    details.getString("org_mobile_no"),
                    details.getString("org_email"),
                    details.getInt("org_country"),
                    details.getInt("org_state"),
                    details.getString("org_address"),
                    details.getString("org_reg_date"),
                    details.getInt("access_level")
                )
                prefs.setCurOrgDetail(Gson().toJson(org))
            }

            prefs.setAccessLevel(details.getInt("access_level"))
        } catch (e: Exception) {
            e.printStackTrace()
        }



        setGotoMainActivity(true)
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