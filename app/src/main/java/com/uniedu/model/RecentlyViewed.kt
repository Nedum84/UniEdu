package com.uniedu.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uniedu.room.TableNames

@Entity(tableName = TableNames.TABLE_RECENTLY_VIEWED)
class RecentlyViewed (
    @PrimaryKey(autoGenerate = true)
    val view_id:Int,
    val item_viewed:String = "video",//video, book, item_for_sale
    val item_id:String  //ID of the video, book or item_for_sale
)