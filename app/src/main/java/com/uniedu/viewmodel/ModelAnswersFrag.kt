package com.uniedu.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.uniedu.model.*
import com.uniedu.network.RetrofitPOST
import com.uniedu.network.ServerResponse
import com.uniedu.repository.RepoAnswersFrag
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

class ModelAnswersFrag(question: Questions, application: Application) : AndroidViewModel(application) {

    private val database = DatabaseRoom.getDatabaseInstance(application)
    private val viewModelJob = SupervisorJob()//OR Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val answerDetailsRepo = RepoAnswersFrag(database, question)
    private val prefs = ClassSharedPreferences(application)


    init {
        viewModelScope.launch {
            answerDetailsRepo.getAnswers()
        }

    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
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


    //Add answer to db
    fun addToDb(answers: List<Answers?>){
        viewModelScope.launch {
            answerDetailsRepo.addAnswer(answers as List<Answers>)
        }
    }

    //bookmark question
    fun bookMarkQuestion(){
        val bookStatus = _curQuestion.value?.is_bookmarked as Boolean
        _curQuestion.value?.is_bookmarked = !bookStatus
        val qDB = database.questionsDao
        viewModelScope.launch {
            qDB.upSertSingle(_curQuestion.value!!)
        }
    }

    fun isQuestionBookmarked(context: Activity):Int{
        return if (curQuestion.value?.is_bookmarked as Boolean){
            Color.RED
        }else{
            Color.BLACK
        }
    }

    fun isMyQuestion():Int{
        return if (prefs.getCurUserDetail().user_id == curQuestion.value!!.question_from.toInt()) View.VISIBLE else View.GONE
    }

    //Submit LIKE Btn pressed
    fun submitLike(answer: Answers) {
        if (answer.is_liked) return

        viewModelScope.launch {
            // Get the Deferred object for our Retrofit request
            val addLikeBtn = RetrofitPOST.retrofitWithJsonResponse.create(AddLikeBtn::class.java)

            addLikeBtn.likeQuestion(
                user_id = prefs.getCurUserDetail().user_id,
                answer_id = answer.answer_id
            ).enqueue(object :Callback<ServerResponse>{
                override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                    ClassAlertDialog(getApplication()).toast("Network error")
                }

                override fun onResponse(call: Call<ServerResponse>,response: Response<ServerResponse>) {
                    if (!response.isSuccessful){
                        ClassAlertDialog(getApplication()).toast("Network error")
                    }else{
                        if (!response.body()?.success!!){
                            ClassAlertDialog(getApplication()).toast("Network error")
                            ClassAlertDialog(getApplication()).toast(response.body()?.respMessage as String)
                        }else{
                            answer.is_liked = true
                            try {
                                viewModelScope.launch {
                                    answerDetailsRepo.addAnswer(listOf(answer))
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }

            })
        }
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

interface AddLikeBtn{

    @POST("add_question_like.php")
    fun likeQuestion(
        @Part("user_id") user_id: Int,
        @Part("answer_id") answer_id: Int
    ):Call<ServerResponse>
}
