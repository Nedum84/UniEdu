package com.uniedu.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.uniedu.Event
import com.uniedu.model.*
import com.uniedu.repository.RepoQuestionsFrag
import com.uniedu.room.DatabaseRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ModelQuestionsFrag(application: Application) : AndroidViewModel(application) {

    private val database = DatabaseRoom.getDatabaseInstance(application)
    private val viewModelJob = SupervisorJob()//OR Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val schoolId = 56
    private val answerDetailsRepo = RepoQuestionsFrag(database)


    init {
        refreshQuestion()
    }
    fun refreshQuestion(qSearchParam: QSearchParam = QSearchParam(schoolId)){
        viewModelScope.launch {
            answerDetailsRepo.getQuestions(qSearchParam)
        }
    }
    val questions: LiveData<List<Questions>> = answerDetailsRepo.questions
    val feedBack = answerDetailsRepo.feedBack



    //Current Question
    val qSearchParam: LiveData<Event<QSearchParam>> get() = _qSearchParam
    private val _qSearchParam = MutableLiveData<Event<QSearchParam>>().apply {
        value = Event(QSearchParam(schoolId))
    }
    fun qSearchQuery(data: QSearchParam) {
        _qSearchParam.value = Event(data)
//        refreshQuestion(qSearchParam.value!!.getContentIfNotHandled()!!)
        refreshQuestion(qSearchParam.value!!.peekContent())
    }













    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ModelQuestionsFrag::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ModelQuestionsFrag(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
