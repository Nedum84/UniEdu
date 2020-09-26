package com.uniedu.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.uniedu.model.Answers
import com.uniedu.room.TableNames

@Dao
interface AnswersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upSert(list: List<Answers>)


    @Query("SELECT * from ${TableNames.TABLE_ANSWERS} WHERE question_id = :id")
    fun getAnswerById(id: Int): LiveData<List<Answers>>


    @Query("SELECT * FROM ${TableNames.TABLE_ANSWERS} ORDER BY answer_id DESC")
    fun getAllAnswer(): LiveData<List<Answers>>

    @Query("DELETE FROM ${TableNames.TABLE_ANSWERS}")
    fun delete()
}