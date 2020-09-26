package com.uniedu.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.uniedu.Event
import com.uniedu.model.MyDetails
import com.uniedu.model.Questions
import com.uniedu.model.Schools
import com.uniedu.network.RetrofitPOST
import com.uniedu.network.ServerResponse
import com.uniedu.repository.RepoCourses
import com.uniedu.repository.RepoSchools
import com.uniedu.room.DatabaseRoom
import com.uniedu.utils.ClassAlertDialog
import com.uniedu.utils.ClassSharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Part

class HomeViewModel(application: Application, myDetails: MyDetails) : AndroidViewModel(application) {
    val app = application
    val prefs = ClassSharedPreferences(app).getCurUserDetail()

    private val database = DatabaseRoom.getDatabaseInstance(app)
    private val viewModelJob = SupervisorJob()//OR Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    private val schoolRepo = RepoSchools(database)

    val school: LiveData<Event<Schools>> get() = _school
    private val _school = MutableLiveData<Event<Schools>>()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
//        _school.value = Event((schoolRepo.schools().value!!.filter { it.school_id.toString() == myDetails.user_school })[0])
    }

    fun changeSchool(sch:Schools){
        if (prefs.user_school == sch.school_id.toString()) return

        val retrofit = RetrofitPOST.retrofitWithJsonResponse.create(ChangeUserSchool::class.java)
        retrofit.change(
            user_id = prefs.user_id,
            school_id = sch.school_id
        ).enqueue(object  : Callback<ServerResponse>{
            override fun onFailure(call: Call<ServerResponse>, t: Throwable) {

            }

            override fun onResponse(call: Call<ServerResponse>,response: Response<ServerResponse>) {
                if (!response.isSuccessful){
                    ClassAlertDialog(app).toast("Network error")
                }else{
                    val resp = response.body()
                    if (!(resp!!.success as Boolean)){
                        ClassAlertDialog(app).toast(resp.respMessage!!)
                    }else{

                        val schDB = database.schoolsDao()
                        viewModelScope.launch {
                            schDB.upSert(listOf(sch))
                        }
                        _school.value = Event(sch)
                    }
                }
            }
        })
    }






    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(private val app: Application, val myDetails: MyDetails) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(app, myDetails) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}

interface ChangeUserSchool{

    @POST("change_school.php")
    fun change(
        @Part("user_id") user_id: Int,
        @Part("school_id") school_id:Int
    ):Call<ServerResponse>
}