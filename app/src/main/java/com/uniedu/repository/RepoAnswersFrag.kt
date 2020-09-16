package com.uniedu.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.uniedu.Event
import com.uniedu.model.Answers
import com.uniedu.model.Questions
import com.uniedu.network.RetrofitConstantGET
import com.uniedu.room.DatabaseRoom
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import retrofit2.http.Query

class RepoAnswersFrag(private val database: DatabaseRoom, private val question: Questions) {

    val answers: LiveData<List<Answers>> = database.answersDao().getAllAnswer()
    val feedBack:LiveData<Event<String>> get() = _feedBack
    private val _feedBack  = MutableLiveData<Event<String>>().apply {
        value = Event("success")
    }

    suspend fun getAnswers(){
        val answerService = RetrofitConstantGET.retrofit
            .create(AnswerService::class.java)
            .getAnswersAsync("${question.school_id}", "${question.question_id}")

        withContext(Dispatchers.IO) {
            try {
                val listResult = answerService.await()
//                database.answersDao().delete()
                database.answersDao().upSertAnswer(listResult)
            } catch (e: Exception) {
                e.printStackTrace()
                _feedBack.value = Event("network_error")
            }
        }
    }
}

interface AnswerService {

    @GET("get_rape_detail.php")
    fun getAnswersAsync(
        @Query("school_id") school_id: String,
        @Query("question_id") question_id: String
    ): Deferred<List<Answers>>
}
