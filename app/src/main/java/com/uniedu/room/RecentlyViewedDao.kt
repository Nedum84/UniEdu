package com.uniedu.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.uniedu.model.RecentlyViewed
import com.uniedu.room.TableNames.Companion.TABLE_RECENTLY_VIEWED


@Dao
interface RecentlyViewedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upSert(list: List<RecentlyViewed>)

    @Query("SELECT * from $TABLE_RECENTLY_VIEWED WHERE item_viewed = :id ORDER BY view_id DESC")
    suspend fun getByItemType(id: String): RecentlyViewed?


    @Query("SELECT * FROM $TABLE_RECENTLY_VIEWED ORDER BY view_id DESC")
    fun getAll(): LiveData<List<RecentlyViewed>>

    @Query("DELETE FROM $TABLE_RECENTLY_VIEWED")
    fun delete()

}

