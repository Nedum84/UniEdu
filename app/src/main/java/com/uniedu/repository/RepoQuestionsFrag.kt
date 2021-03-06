package com.uniedu.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.uniedu.model.QSearchParam
import com.uniedu.model.Questions
import com.uniedu.network.RetrofitConstantGET
import com.uniedu.room.DatabaseRoom
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import retrofit2.http.Query

class RepoQuestionsFrag(private val database: DatabaseRoom) {

    val questions : LiveData<List<Questions>> = database.questionsDao.getAll()

    val feedBack:LiveData<String> get() = _feedBack
    private val _feedBack  = MutableLiveData<String>().apply {
        value = "success"
    }

    suspend fun getQuestions(qSearchParam: QSearchParam){
        val questionService = RetrofitConstantGET.retrofit
            .create(QuestionService::class.java)
            .getQuestionsAsync(
                "${qSearchParam.school_id}",
                "${qSearchParam.course_id}",
                "${qSearchParam.answered}",
                "${qSearchParam.start_from}")

        withContext(Dispatchers.IO) {
            try {
                val listResult = questionService.await()
                if (qSearchParam.start_from == 0){
                    database.questionsDao.deleteNotMySchools(qSearchParam.school_id)
                    database.questionsDao.upSert(listResult)
                }else{
                    database.questionsDao.upSert(listResult)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _feedBack.postValue("network_error")
            }
        }
    }


    suspend fun addQuestion(questions: List<Questions>){
        withContext(Dispatchers.IO){
            try {
                database.questionsDao.upSert(questions)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}


interface QuestionService {

    @GET("add_question.php")
    fun getQuestionsAsync(
        @Query("school_id") school_id: String,
        @Query("course_id") course_id: String,
        @Query("answered") answered: String,
        @Query("start_from") start_from: String,
        @Query("record_per_page") record_per_page: String = "10"
    ): Deferred<List<Questions>>
}
