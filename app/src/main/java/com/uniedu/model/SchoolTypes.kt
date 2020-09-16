package com.uniedu.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uniedu.room.TableNames

@Entity(tableName = TableNames.TABLE_SCHOOL_TYPES)
class SchoolTypes (
    @PrimaryKey
    val school_type_id : Int,
    val school_type_name:String
)