package com.uniedu.model

import android.os.Parcelable
import android.text.Spanned
import android.widget.TextView
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uniedu.extension.getImgPaths
import com.uniedu.extension.removeImgTags
import com.uniedu.room.DatabaseRoom
import com.uniedu.room.TableNames
import com.uniedu.utils.ClassDateAndTime
import com.uniedu.utils.ClassHtmlFormater
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.*

@Parcelize
@Entity(tableName = TableNames.TABLE_QUESTIONS)
class Questions(
    @PrimaryKey
    @ColumnInfo(name = "question_id")
    val question_id :Int,
    @ColumnInfo(name = "question_body")
    val question_body:String,
    @ColumnInfo(name = "question_image_path")
    val question_image_path:String,
    @ColumnInfo(name = "question_from")
    val question_from:String,
    @ColumnInfo(name = "question_from_photo")
    val question_from_photo:String,
    @ColumnInfo(name = "question_from_name")
    val question_from_name:String,
    @ColumnInfo(name = "question_no_of_answer")
    val question_no_of_answer:String,
    @ColumnInfo(name = "question_date")
    val question_date:String,
    @ColumnInfo(name = "course_id")
    val course_id:String,
    @ColumnInfo(name = "school_id")
    val school_id:String,
    @ColumnInfo(name = "is_bookmarked")
    var is_bookmarked:Boolean = false
):Parcelable{

    fun courseCode(db:DatabaseRoom) = runBlocking{
        withContext(Dispatchers.Default) {
            db.coursesDao.getById(
                course_id.toInt()
            )
        }
    }?.course_code


//    fun courseCode2(db:DatabaseRoom, view:TextView){
//
//        CoroutineScope(Dispatchers.Main).launch {
//            try {
//                val course = db.coursesDao.getById(course_id.toInt())
//                view.text = course!!.course_code
//            } catch (e: Exception) {
//                e.printStackTrace()
//                view.text = course_id
//            }
//        }
//
//    }

    fun questionBody():Spanned{
        val imgPaths = question_body.getImgPaths()
        var qBody = question_body.removeImgTags()


        qBody =  if (imgPaths.isNotEmpty()){
            val body = if (qBody.isNotEmpty()) ": $qBody" else ""
            "(Image) $body"
        }else{
            qBody
        }
        return ClassHtmlFormater().fromHtml(qBody)

//        for (i in imgPaths){
//            qBody = qBody.replace(i,"")
//        }
//        qBody.replace("<img src=","")
//        qBody.replace("<img src=","")
    }


    fun posterPhoto():String{
        return question_from_photo
    }

    fun time() = ClassDateAndTime().checkDateTimeFirst(question_date.toLong())
}