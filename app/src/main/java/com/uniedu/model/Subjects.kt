package com.uniedu.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uniedu.room.TableNames

@Entity(tableName = TableNames.TABLE_SUBJECT)
class Subjects (
    @PrimaryKey(autoGenerate = true)
    val subject_id:Int,
    val subject_name:String,
    var subject_banner: String = "",
    var arr_order: Int
)