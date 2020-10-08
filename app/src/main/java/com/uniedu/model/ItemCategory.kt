package com.uniedu.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uniedu.room.TableNames

@Entity(tableName = TableNames.TABLE_ITEM_CATEGORY)
class ItemCategory (
    @PrimaryKey
    val category_id:Int,
    val category_name: String,
    val category_banner: String="",
    val arr_order:Int
)
