package com.uniedu.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.uniedu.model.Courses
import com.uniedu.model.MyDetails
import com.uniedu.model.QSearchParam
import com.uniedu.model.Questions
import com.uniedu.network.RetrofitConstantGET
import com.uniedu.room.DatabaseRoom
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import retrofit2.http.Query

class RepoCourses(private val database: DatabaseRoom) {


    val courses : LiveData<List<Courses>> = database.coursesDao.getAllCourses()

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
                _feedBack.value = "network_error"
            }
        }
    }
}

interface CourseService {

    @GET("get_rape_detail.php")
    fun getCoursesAsync(
        @Query("school_id") school_id: String
    ): Deferred<List<Courses>>
}
