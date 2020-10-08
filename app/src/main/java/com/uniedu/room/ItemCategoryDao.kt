package com.uniedu.room


import androidx.lifecycle.LiveData
import androidx.room.*
import com.uniedu.model.ItemCategory
import com.uniedu.room.TableNames.Companion.TABLE_ITEM_CATEGORY

@Dao
interface ItemCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upSert(list: List<ItemCategory>)

    @Query("SELECT * from $TABLE_ITEM_CATEGORY WHERE category_id  = :id")
    fun getById(id: Long): ItemCategory?


    @Query("SELECT * FROM $TABLE_ITEM_CATEGORY ORDER BY arr_order DESC")
    fun getAll(): LiveData<List<ItemCategory>>


    @Query("SELECT * FROM $TABLE_ITEM_CATEGORY WHERE category_id LIKE :id  ORDER BY arr_order DESC")
    fun getAll(id: String): LiveData<List<ItemCategory>>
}

