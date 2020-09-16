package com.uniedu.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uniedu.room.TableNames
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = TableNames.TABLE_COURSES)
class Courses (
    @PrimaryKey
    @ColumnInfo(name = "course_id")
    val course_id:Int,
    @ColumnInfo(name = "course_code")
    var course_code: String,
    @ColumnInfo(name = "course_title")
    var course_title: String,
    @ColumnInfo(name = "course_banner")
    var course_banner: String,
    @ColumnInfo(name = "course_semester")
    var course_semester: String,
    @ColumnInfo(name = "course_no_of_topics")
    var course_no_of_topics: String,
    @ColumnInfo(name = "course_price")
    var course_price: String,
    @ColumnInfo(name = "course_tutor")
    var course_tutor: String,
    @ColumnInfo(name = "is_video_for_sale")
    var is_video_for_sale: String,
    @ColumnInfo(name = "school_id")
    var school_id: String,
    @ColumnInfo(name = "arr_order")
    var arr_order: Int
):Parcelable{

    fun courseCodeAndTitle():String{
        return "$course_code ($course_title)"
    }
    fun courseCodeAndTitle2():String{
        return "$course_code - $course_title"
    }

    fun noOfTopics() = "$course_no_of_topics topics"

    fun courseTutor() = "By $course_tutor"

    fun coursePrice() = "₦$course_price.00"
}
