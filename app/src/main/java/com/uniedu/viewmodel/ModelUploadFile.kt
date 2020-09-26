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

class ModelUploadFile : ViewModel() {


    init {

    }

    //Current Question
    val imageUploaded: LiveData<Event<String>> get() = _imageUploaded
    private val _imageUploaded = MutableLiveData<Event<String>>()
    fun isFileUloaded(data: String) {
        _imageUploaded.value = Event(data)
    }







}
