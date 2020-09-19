package com.uniedu.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.uniedu.Event
import com.uniedu.model.*
import com.uniedu.repository.RepoAnswersFrag
import com.uniedu.repository.RepoCourses
import com.uniedu.room.DatabaseRoom
import com.uniedu.utils.ClassAlertDialog
import com.uniedu.utils.ClassSharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.*

class ModelCourses(application: Application) : AndroidViewModel(application) {

    private val database = DatabaseRoom.getDatabaseInstance(application)
    private val viewModelJob = SupervisorJob()//OR Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val coursesRepo = RepoCourses(database)
    private val myDetails = ClassSharedPreferences(application).getCurUserDetail()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
    private val _queryString = MutableLiveData<Event<String>>()
    init {
        viewModelScope.launch {
            coursesRepo.getCourses(myDetails)
        }

        _queryString.value = Event("")
    }

//    val courses: LiveData<List<Courses>> = coursesRepo.courses()
    val feedBack = coursesRepo.feedBack

    val queryString: LiveData<Event<String>> get() = _queryString

    fun setSearchQuery(queryString: String=""){
        val q = queryString.toLowerCase(Locale.ROOT).replace(" ","")
        ClassSharedPreferences(getApplication()).setSearchQuery(q)

        _queryString.value = Event(q)


    }
    fun courses(qString: String=""):LiveData<List<Courses>>{
        return coursesRepo.courses(qString)
    }



    fun addToDb(courses: List<Courses>){
        viewModelScope.launch {
            coursesRepo.addCourse(courses)
        }
    }

    fun refreshCourse(){
        viewModelScope.launch {
            coursesRepo.getCourses(myDetails)
        }
    }



    //Current Question
    val curCourse: LiveData<Event<Courses>> get() = _curCourse
    private val _curCourse = MutableLiveData<Event<Courses>>()
    fun setCurCourse(data: Courses) {
        _curCourse.value = Event(data)
    }













    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ModelCourses::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ModelCourses(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
