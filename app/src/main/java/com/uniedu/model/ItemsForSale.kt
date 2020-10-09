package com.uniedu.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uniedu.extension.getImgPaths
import com.uniedu.extension.getImgPathsWithHttp
import com.uniedu.extension.removeImgTags
import com.uniedu.room.TableNames
import com.uniedu.utils.ClassHtmlFormater
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
    val item_qty:String,
    val item_uploaded_by:String,
    val item_uploaded_by_name:String,
    val item_uploaded_by_photo:String,
    val item_uploaded_by_mobile_no:String,
    val is_item_sold:Boolean = false,
    val school_id:String,
    val is_bookmarked:Boolean = false
):Parcelable{

    fun itemPrice() = "₦$item_price.00"

    fun itemBanner():String{
        val imgPaths = item_description.getImgPathsWithHttp()
        return if (imgPaths.isNullOrEmpty()){ "" }else imgPaths[0]
    }

    fun itemQty() = if(item_qty.isEmpty()||item_qty=="0") "1" else item_qty

    fun sellerName() = "• $item_uploaded_by_name"

    fun sellerNo() = "• $item_uploaded_by_mobile_no"

    fun itemDesc() = if (item_description.isEmpty()) ClassHtmlFormater().fromHtml("<i>empty...</i>") else item_description.removeImgTags()
}