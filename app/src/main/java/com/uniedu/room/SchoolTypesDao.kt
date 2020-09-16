package com.uniedu.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.uniedu.model.SchoolTypes
import com.uniedu.room.TableNames.Companion.TABLE_SCHOOL_TYPES


@Dao
interface SchoolTypesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upSert(list: List<SchoolTypes>)


    @Query("SELECT * from $TABLE_SCHOOL_TYPES WHERE school_type_id = :id")
    suspend fun getById(id: Int): SchoolTypes?


    @Query("SELECT * FROM $TABLE_SCHOOL_TYPES ORDER BY school_type_id DESC")
    fun getAll(): LiveData<List<SchoolTypes>>

    @Query("DELETE FROM $TABLE_SCHOOL_TYPES")
    fun delete()

}

