package com.uniedu.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.uniedu.model.*
import com.uniedu.network.RetrofitConstantGET
import com.uniedu.room.DatabaseRoom
import com.uniedu.utils.ClassSharedPreferences
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import retrofit2.http.Query

class RepoSchools(private val database: DatabaseRoom) {


    fun schools(searchQuery: String?=""):LiveData<List<Schools>>{
        return if (searchQuery.isNullOrEmpty())
            database.schoolsDao().getAll()
        else
            database.schoolsDao().getAll("%$searchQuery%")
    }

    val feedBack:LiveData<String> get() = _feedBack
    private val _feedBack  = MutableLiveData<String>().apply {
        value = "success"
    }

    suspend fun getSchools(myDetails: MyDetails){
        val schoolService = RetrofitConstantGET.retrofit
            .create(SchoolService::class.java)
            .getCoursesAsync(filter = myDetails.user_school)

        withContext(Dispatchers.IO) {
            try {
                val listResult = schoolService.await()
                database.schoolsDao().upSert(listResult)
            } catch (e: Exception) {
                e.printStackTrace()
                _feedBack.postValue("network_error")
            }
        }
    }
    suspend fun addSchool(schools: List<Schools>){
        withContext(Dispatchers.IO){
            try {
                database.schoolsDao().upSert(schools)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

interface SchoolService {

    @GET("add_school.php")
    fun getCoursesAsync(
        @Query("filter") filter: String
    ): Deferred<List<Schools>>
}
