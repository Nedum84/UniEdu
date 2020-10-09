package com.uniedu.model

import android.os.Parcelable
import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.uniedu.room.TableNames
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = TableNames.TABLE_TOPICS)
class TopicVideos (
    @PrimaryKey
    val topic_id:Int,
    val topic_name:String,
    val topic_banner:String,
    val course_id:String,
    val school_id:String,
    val arr_order:Int
):Parcelable{

//    @IgnoredOnParcel
//    @TypeConverters(Converters::class)
//    val videos: List<Videos>? = null

    fun getTitle(idx:Int) = "$idx. $topic_name"
}


class Converters {

    @TypeConverter
    fun fromGroupTaskMemberList(value: List<Videos>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Videos>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toGroupTaskMemberList(value: String): List<Videos> {
        val gson = Gson()
        val type = object : TypeToken<List<Videos>>() {}.type
        return gson.fromJson(value, type)
    }
}

