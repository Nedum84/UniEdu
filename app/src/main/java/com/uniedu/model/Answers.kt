package com.uniedu.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uniedu.room.TableNames
import com.uniedu.utils.ClassDateAndTime
import com.uniedu.utils.ClassSharedPreferences
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = TableNames.TABLE_ANSWERS)
class Answers (
    @PrimaryKey
    val answer_id:Int,
    val answer_body: String,
    val answer_image_path: String,
    val answer_from: String,
    val answer_from_photo: String,
    val answer_from_name: String,
    val answer_no_of_like: String,
    var is_liked: Boolean,
    val answer_date: String,
    val question_id: String,
    val school_id: String
):Parcelable{

    fun time() = ClassDateAndTime().checkDateTimeFirst(answer_date.toLong())

}