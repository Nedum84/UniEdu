package com.uniedu.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.uniedu.model.Questions
import com.uniedu.room.TableNames.Companion.TABLE_QUESTIONS


@Dao
interface QuestionsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upSert(list: List<Questions>)

    @Update
    fun update(rapeSupportType: Questions)


    @Query("SELECT * from $TABLE_QUESTIONS WHERE question_id = :id")
    suspend fun getById(id: Int): LiveData<Questions>


    @Query("SELECT * FROM $TABLE_QUESTIONS ORDER BY question_id DESC")
    fun getAll(): LiveData<List<Questions>>

    @Query("DELETE FROM $TABLE_QUESTIONS WHERE school_id = :id")
    fun deleteNotMySchools(id: Int)

}

