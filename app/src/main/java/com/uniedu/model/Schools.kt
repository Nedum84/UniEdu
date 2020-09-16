package com.uniedu.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uniedu.room.TableNames
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = TableNames.TABLE_SCHOOLS)
class Schools (
    @PrimaryKey
    val school_id:Int,
    val school_name: String,
    val school_code:String,
    val school_logo:String,
    val school_motto:String,
    val school_website:String,
    val school_courses:String,
    val arr_order:Int
):Parcelable