package com.uniedu.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.uniedu.Event
import com.uniedu.model.*
import com.uniedu.repository.RepoAnswersFrag
import com.uniedu.repository.RepoCourses
import com.uniedu.repository.RepoEBooks
import com.uniedu.room.DatabaseRoom
import com.uniedu.utils.ClassAlertDialog
import com.uniedu.utils.ClassSharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.*

class ModelEbook(application: Application) : AndroidViewModel(application) {

    private val database = DatabaseRoom.getDatabaseInstance(application)
    private val viewModelJob = SupervisorJob()//OR Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val booksRepo = RepoEBooks(database)
    private val myDetails = ClassSharedPreferences(application).getCurUserDetail()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    val queryString: LiveData<Event<String>> get() = _queryString
    private val _queryString = MutableLiveData<Event<String>>()
    init {
        viewModelScope.launch {
            booksRepo.getEBooks(myDetails)
        }

        _queryString.value = Event("")
    }

    val feedBack = booksRepo.feedBack



    fun setSearchQuery(queryString: String=""){
        val q = queryString.toLowerCase(Locale.ROOT).replace(" ","")
        ClassSharedPreferences(getApplication()).setSearchQuery(q)

        _queryString.value = Event(q)


    }
    fun ebooks(qString: String=""):LiveData<List<EBooks>>{
        return booksRepo.ebooks(qString)
    }



    fun addToDb(books: List<EBooks>){
        viewModelScope.launch {
            booksRepo.addEbook(books)
        }
    }

    fun refreshCourse(){
        viewModelScope.launch {
            booksRepo.getEBooks(myDetails)
        }
    }












    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ModelEbook::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ModelEbook(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
