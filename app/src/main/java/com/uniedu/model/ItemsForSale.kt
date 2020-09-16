package com.uniedu.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uniedu.room.TableNames
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = TableNames.TABLE_ITEM_FOR_SALE)
class ItemsForSale (
    @PrimaryKey
    val item_id:Int,
    val item_name:String,
    val item_description:String,
    val item_image:String,
    val item_category:String,
    val item_price:String,
    val item_qty:Int,
    val item_uploader:String,
    val item_uploader_name:String,
    val is_item_sold:Boolean = false,
    val school_id:String,
    val is_bookmarked:Boolean = false
):Parcelable{

    fun itemPrice() = "₦$item_price.00"
}