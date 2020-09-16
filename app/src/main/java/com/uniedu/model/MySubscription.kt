package com.uniedu.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uniedu.room.TableNames
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = TableNames.TABLE_MY_SUBSCRIPTION)
class MySubscription (
    @PrimaryKey(autoGenerate = true)
    val sub_id:Int,
    val sub_type:String="video",
    val course_id:String,
    val school_id:String,
    val start_date:String,
    val end_date:String
):Parcelable