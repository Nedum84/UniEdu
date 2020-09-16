package com.uniedu.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uniedu.room.TableNames
import com.uniedu.utils.ClassDateAndTime
import kotlinx.android.parcel.Parcelize


@Entity(tableName = TableNames.TABLE_ANSWERS)
class Answers (
    @PrimaryKey
    @ColumnInfo(name = "answer_id")
    val answer_id:Int,
    @ColumnInfo(name = "answer_body")
    val answer_body: String,
    @ColumnInfo(name = "answer_image_path")
    val answer_image_path: String,
    @ColumnInfo(name = "answer_uploader")
    val answer_uploader: String,
    @ColumnInfo(name = "answer_uploader_photo")
    val answer_uploader_photo: String,
    @ColumnInfo(name = "answer_no_of_like")
    val answer_no_of_like: String,
    @ColumnInfo(name = "answer_uploader_name")
    val answer_uploader_name: String,
    @ColumnInfo(name = "is_liked")
    val is_liked: Boolean,
    @ColumnInfo(name = "answer_date")
    val answer_date: String,
    @ColumnInfo(name = "question_id")
    val question_id: String,
    @ColumnInfo(name = "school_id")
    val school_id: String
){

    fun time() = ClassDateAndTime().checkDateTimeFirst(answer_date.toLong())

}