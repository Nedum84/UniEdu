package com.uniedu.room


import androidx.lifecycle.LiveData
import androidx.room.*
import com.uniedu.model.Subjects

@Dao
interface SubjectsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upSert(list: List<Subjects>)

    @Query("SELECT * from ${TableNames.TABLE_SUBJECT} WHERE subject_id  = :id")
    fun getById(id: Long): Subjects?


    @Query("SELECT * FROM ${TableNames.TABLE_SUBJECT} ORDER BY arr_order DESC")
    fun getAll(): LiveData<List<Subjects>>

    @Query("SELECT * FROM ${TableNames.TABLE_SUBJECT} WHERE subject_name LIKE :sQuery  ORDER BY arr_order DESC")
    fun getAll(sQuery: String): LiveData<List<Subjects>>


    @Query("DELETE FROM ${TableNames.TABLE_SUBJECT}")
    fun delete()
}

