package com.uniedu.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.uniedu.model.TopicVideos
import com.uniedu.room.TableNames.Companion.TABLE_TOPICS


@Dao
interface TopicsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upSert(list: List<TopicVideos>)

    @Query("DELETE FROM $TABLE_TOPICS WHERE school_id =:id ")
    fun deleteNotMySchools(id: Int)


    @Query("SELECT * from $TABLE_TOPICS WHERE topic_id = :id")
    suspend fun getById(id: Int): TopicVideos?


    @Query("SELECT * FROM $TABLE_TOPICS ORDER BY arr_order DESC")
    fun getAll(): LiveData<List<TopicVideos>>

    @Query("DELETE FROM $TABLE_TOPICS")
    fun delete()

}

