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
    val book_id:Int,
    val book_title: String,
    val book_author: String?="",
    val book_desc: String,
    val book_size: String,
    val book_no_of_pages: String,
    val book_url_path: String,
    val pdf_image_cover: String,
    val book_type: String,//pdf or image
    val book_uploaded_time: String,
    val book_uploaded_by: String,
    val book_uploaded_by_name: String,
    val course_id: String,
    val school_id: String,
    val book_no_of_download: Int = 0,
    val is_bookmarked:Boolean = false
):Parcelable{


    fun courseCode():String{
        return  course_id
    }
    fun bookType() = if(book_type=="pdf") "pdf" else "photo"

    fun bookCover() = if(book_type=="pdf") pdf_image_cover else book_url_path
}