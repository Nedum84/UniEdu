package com.uniedu.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.uniedu.model.Schools
import com.uniedu.room.TableNames.Companion.TABLE_SCHOOLS


@Dao
interface SchoolsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upSert(list: List<Schools>)


    @Query("SELECT * from $TABLE_SCHOOLS WHERE school_id = :id")
    suspend fun getById(id: Int): Schools?


    @Query("SELECT * FROM $TABLE_SCHOOLS ORDER BY arr_order DESC")
    fun getAll(): LiveData<List<Schools>>

    @Query("DELETE FROM $TABLE_SCHOOLS")
    fun delete()

}

