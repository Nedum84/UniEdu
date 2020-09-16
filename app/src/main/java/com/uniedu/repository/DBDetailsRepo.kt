package com.uniedu.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.uniedu.model.Courses
import com.uniedu.model.Questions
import com.uniedu.room.DatabaseRoom

class DBDetailsRepo(db: DatabaseRoom) {

    val questions : LiveData<List<Questions>> = db.questionsDao.getAll()
    val school = db.schoolTypesDao.getAll()
    val topic = db.topicsDao().getAll()
    val itemsForSale = db.itemsForSaleDao()
    val eBooks = db.eBooksDao.getAllBooks()



}