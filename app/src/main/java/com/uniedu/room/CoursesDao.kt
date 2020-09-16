package com.uniedu.room


import androidx.lifecycle.LiveData
import androidx.room.*
import com.uniedu.model.Courses
import com.uniedu.model.Questions
import com.uniedu.room.TableNames.Companion.TABLE_COURSES

/**
 * Defines methods for using the SleepNight class with Room.
 */
@Dao
interface CoursesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upSert(list: List<Courses>)//upsert -> Insert and/or update

    @Query("DELETE FROM $TABLE_COURSES WHERE school_id =:id ")
    fun deleteNotMySchools(id: Int)

    @Query("SELECT * from ${TABLE_COURSES} WHERE course_id = :id")
    suspend fun getById(id: Int): Courses?

    @Query("SELECT * FROM $TABLE_COURSES ORDER BY course_id DESC")
    fun getAllCourses(): LiveData<List<Courses>>

}

