package com.uniedu.extension

import android.app.Application
import com.uniedu.model.Courses
import com.uniedu.room.DatabaseRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


fun Int.getCourse(app: Application):Courses{
    val db = DatabaseRoom.getDatabaseInstance(app)


    val course = (db.coursesDao.getById(this@getCourse))

    return  course!!
}