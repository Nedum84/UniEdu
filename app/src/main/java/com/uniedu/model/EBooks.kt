package com.uniedu.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uniedu.room.TableNames
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = TableNames.TABLE_EBOOKS)
class EBooks (
    @PrimaryKey
    @ColumnInfo(name = "book_id")
    val book_id:Int,
    @ColumnInfo(name = "book_title")
    val book_title: String,
    @ColumnInfo(name = "book_url_path")
    val book_url_path: String,
    @ColumnInfo(name = "book_cover")
    val book_cover: String,
    @ColumnInfo(name = "book_uploaded_time")
    val book_uploaded_time: String,
    @ColumnInfo(name = "book_uploader")
    val book_uploader: String,
    @ColumnInfo(name = "book_uploader_name")
    val book_uploader_name: String,
    @ColumnInfo(name = "course_id")
    val course_id: String,
    @ColumnInfo(name = "school_id")
    val school_id: String,
    @ColumnInfo(name = "is_bookmarked")
    val is_bookmarked:Boolean = false
):Parcelable{

    fun courseCode():String{

        return  course_id
    }
}