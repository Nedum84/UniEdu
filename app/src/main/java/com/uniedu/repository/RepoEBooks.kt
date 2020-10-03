package com.uniedu.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.uniedu.model.*
import com.uniedu.network.RetrofitConstantGET
import com.uniedu.room.DatabaseRoom
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import retrofit2.http.Query

class RepoEBooks(private val database: DatabaseRoom) {


    fun ebooks(searchQuery: String?=""):LiveData<List<EBooks>>{

        return if (searchQuery.isNullOrEmpty())
            database.eBooksDao.getAllBooks()
        else
            database.eBooksDao.getAllBooks("%$searchQuery%")
    }

    val feedBack:LiveData<String> get() = _feedBack
    private val _feedBack  = MutableLiveData<String>().apply {
        value = "success"
    }

    suspend fun getEBooks(myDetails: MyDetails){
        val service = RetrofitConstantGET.retrofit
            .create(EBookService::class.java)
            .getBooksAsync(myDetails.user_school)

        withContext(Dispatchers.IO) {
            try {
                val listResult = service.await()
                database.eBooksDao.upSert(listResult)
            } catch (e: Exception) {
                e.printStackTrace()
                _feedBack.postValue("network_error")
            }
        }
    }
    suspend fun addEbook(courses: List<EBooks>){
        withContext(Dispatchers.IO){
            try {
                database.eBooksDao.upSert(courses)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

interface EBookService {

    @GET("add_ebook.php")
    fun getBooksAsync(
        @Query("school_id") school_id: String
    ): Deferred<List<EBooks>>
}
