package com.uniedu.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.uniedu.model.Topics
import com.uniedu.room.TableNames.Companion.TABLE_TOPICS


@Dao
interface TopicsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upSert(list: List<Topics>)

    @Query("SELECT * from $TABLE_TOPICS WHERE topic_id = :id")
    suspend fun getById(id: Int): Topics?


    @Query("SELECT * FROM $TABLE_TOPICS ORDER BY arr_order DESC")
    fun getAll(): LiveData<List<Topics>>

    @Query("DELETE FROM $TABLE_TOPICS")
    fun delete()

}

