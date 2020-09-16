package com.uniedu.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.uniedu.model.*
import com.uniedu.repository.RepoAnswersFrag
import com.uniedu.room.DatabaseRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ModelAnswersFrag(question: Questions, application: Application) : AndroidViewModel(application) {

    private val database = DatabaseRoom.getDatabaseInstance(application)
    private val viewModelJob = SupervisorJob()//OR Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val answerDetailsRepo = RepoAnswersFrag(database, question)


    init {
        viewModelScope.launch {
            answerDetailsRepo.getAnswers()
        }
    }
    fun refreshRapeDetail(){
        viewModelScope.launch {
            answerDetailsRepo.getAnswers()
        }
    }
    val answers: LiveData<List<Answers>> = answerDetailsRepo.answers
    val feedBack = answerDetailsRepo.feedBack



    //Current Question
    val curQuestion: LiveData<Questions> get() = _curQuestion
    private val _curQuestion = MutableLiveData<Questions>().apply {
        value = question
    }
    fun setCurQuestion(data: Questions) {
        _curQuestion.value = (data)
    }













    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(private val question: Questions, private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ModelAnswersFrag::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ModelAnswersFrag(question, app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
