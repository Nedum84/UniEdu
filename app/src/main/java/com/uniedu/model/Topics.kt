package com.uniedu.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uniedu.room.TableNames
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = TableNames.TABLE_TOPICS)
class Topics (
    @PrimaryKey
    val topic_id:Int,
    val topic_name:String,
    val topic_banner:String,
    val course_id:String,
    val school_id:String,
    val arr_order:Int,
    val videos: List<Videos>? = null
):Parcelable{

    fun getTitle(idx:Int) = "$idx. $topic_name"
}