package com.uniedu.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uniedu.room.TableNames
import com.uniedu.utils.ClassDateAndTime
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = TableNames.TABLE_VIDEOS)
class Videos (
    @PrimaryKey
    val video_id:Int,
    val video_title:String,
    val video_youtube_link:String,
    val video_url:String,
    val video_description:String,
    val video_banner:String,
    val video_duration:Int,
    val video_watched_duration:Int = 0,
    val time_uploaded:String,
    val tutor_id:String,
    val tutor_name:String,
    val topic_id:String,
    val school_id:String,
    val arr_order:String,
    val is_bookmarked:Boolean = false
):Parcelable{

    fun videoDuration() = ClassDateAndTime().checkDateTimeFirst(video_duration.toLong())

    fun videoWatchedProgress() = (video_watched_duration/1)*100
//    fun videoWatchedProgress() = (video_watched_duration/video_duration)*100

    companion object{
        var INDEX = 0
    }
}