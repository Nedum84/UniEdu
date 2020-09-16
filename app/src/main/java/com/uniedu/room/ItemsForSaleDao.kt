package com.ng.rapetracker.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.uniedu.model.ItemsForSale
import com.uniedu.room.TableNames

@Dao
interface ItemsForSaleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upSert(list: List<ItemsForSale>)

    @Query("SELECT * from ${TableNames.TABLE_ITEM_FOR_SALE} WHERE item_id  = :id")
    suspend fun getById(id: Long): ItemsForSale?


    @Query("SELECT * FROM ${TableNames.TABLE_ITEM_FOR_SALE} ORDER BY item_id DESC")
    fun getAll(): LiveData<List<ItemsForSale>>

    @Query("DELETE FROM ${TableNames.TABLE_ITEM_FOR_SALE}")
    fun delete()
}