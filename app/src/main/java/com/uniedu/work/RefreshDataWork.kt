package com.uniedu.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.uniedu.model.QSearchParam
import com.uniedu.repository.RepoQuestionsFrag
import com.uniedu.room.DatabaseRoom
import com.uniedu.utils.ClassSharedPreferences
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val database = DatabaseRoom.getDatabaseInstance(applicationContext)
        val mySchool = ClassSharedPreferences(applicationContext).getCurUserDetail().user_school.toInt()

        val getQuestion = RepoQuestionsFrag(database)

        return try {
            getQuestion.getQuestions(QSearchParam(mySchool))
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }



    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

}
