package com.uniedu.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.uniedu.model.Courses
import com.uniedu.model.MyDetails
import com.uniedu.model.QSearchParam
import com.uniedu.model.Questions
import com.uniedu.network.RetrofitConstantGET
import com.uniedu.room.DatabaseRoom
import com.uniedu.utils.ClassSharedPreferences
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import retrofit2.http.Query

class RepoCourses(private val database: DatabaseRoom) {


//    val courses : LiveData<List<Courses>> = database.coursesDao.getAllCourses()
    fun courses(searchQuery: String?=""):LiveData<List<Courses>>{

        return if (searchQuery.isNullOrEmpty())
            database.coursesDao.getAllCourses()
        else
            database.coursesDao.getAllCourses("%$searchQuery%")
    }

    val feedBack:LiveData<String> get() = _feedBack
    private val _feedBack  = MutableLiveData<String>().apply {
        value = "success"
    }

    suspend fun getCourses(myDetails: MyDetails){
        val courseService = RetrofitConstantGET.retrofit
            .create(CourseService::class.java)
            .getCoursesAsync(myDetails.user_school)

        withContext(Dispatchers.IO) {
            try {
                val listResult = courseService.await()
                database.coursesDao.deleteNotMySchools(myDetails.user_school.toInt())
                database.coursesDao.upSert(listResult)
            } catch (e: Exception) {
                e.printStackTrace()
                _feedBack.postValue("network_error")
            }
        }
    }
    suspend fun addCourse(courses: List<Courses>){
        withContext(Dispatchers.IO){
            try {
                database.coursesDao.upSert(courses)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

interface CourseService {

    @GET("get_courses.php")
    fun getCoursesAsync(
        @Query("school_id") school_id: String
    ): Deferred<List<Courses>>
}
