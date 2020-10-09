package com.uniedu.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.uniedu.Event
import com.uniedu.model.*
import com.uniedu.repository.RepoAnswersFrag
import com.uniedu.repository.RepoCourses
import com.uniedu.repository.RepoTopicVideos
import com.uniedu.room.DatabaseRoom
import com.uniedu.utils.ClassAlertDialog
import com.uniedu.utils.ClassSharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.*

class ModelTopicVideos(application: Application) : AndroidViewModel(application) {

    private val database = DatabaseRoom.getDatabaseInstance(application)
    private val viewModelJob = SupervisorJob()//OR Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val topicVideosRepo = RepoTopicVideos(database)
    private val myDetails = ClassSharedPreferences(application).getCurUserDetail()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
    init {
        viewModelScope.launch {
            topicVideosRepo.getTopicVideos(myDetails)
        }

    }

    val topicVideos: LiveData<List<TopicVideos>> = topicVideosRepo.topicVideos
    val videos = topicVideosRepo.videos



    fun refresh(){
        viewModelScope.launch {
            topicVideosRepo.getTopicVideos(myDetails)
        }
    }



    //Current Video
    val curVideo: LiveData<Event<Videos>> get() = _curVideo
    private val _curVideo = MutableLiveData<Event<Videos>>()
    fun setCurVid(data: Videos) {
        _curVideo.value = Event(data)
    }













    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ModelTopicVideos::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ModelTopicVideos(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
