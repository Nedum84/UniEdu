package com.uniedu.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.uniedu.model.Videos
import com.uniedu.room.TableNames.Companion.TABLE_VIDEOS


@Dao
interface VideosDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upSert(list: List<Videos>)


    @Query("SELECT * from $TABLE_VIDEOS WHERE video_id = :id")
    suspend fun getById(id: Int): Videos?


    @Query("SELECT * FROM $TABLE_VIDEOS ORDER BY arr_order DESC")
    fun getAll(): LiveData<List<Videos>>

    @Query("DELETE FROM $TABLE_VIDEOS")
    fun delete()

}

