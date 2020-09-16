package com.uniedu.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.uniedu.model.MySubscription
import com.uniedu.room.TableNames.Companion.TABLE_MY_SUBSCRIPTION


@Dao
interface MySubscriptionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upSert(list: List<MySubscription>)


    @Query("SELECT * from $TABLE_MY_SUBSCRIPTION WHERE sub_id = :id")
    suspend fun getById(id: Int): MySubscription?


    @Query("SELECT * FROM $TABLE_MY_SUBSCRIPTION ORDER BY end_date DESC")
    fun getAll(): LiveData<List<MySubscription>>

    @Query("DELETE FROM $TABLE_MY_SUBSCRIPTION")
    fun delete()

}

