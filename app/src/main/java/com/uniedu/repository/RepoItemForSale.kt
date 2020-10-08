package com.uniedu.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.uniedu.model.*
import com.uniedu.network.RetrofitConstantGET
import com.uniedu.room.DatabaseRoom
import com.uniedu.utils.ClassSharedPreferences
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import retrofit2.http.Query

class RepoItemForSale(private val database: DatabaseRoom) {


    fun itemForSale(searchQuery: String?=""):LiveData<List<ItemsForSale>>{

        return if (searchQuery.isNullOrEmpty())
            database.itemsForSaleDao().getAll()
        else
            database.itemsForSaleDao().getAll("%$searchQuery%")
    }

    val feedBack:LiveData<String> get() = _feedBack
    private val _feedBack  = MutableLiveData<String>().apply {
        value = "success"
    }

    suspend fun getItems(myDetails: MyDetails){
        val itemsService = RetrofitConstantGET.retrofit
            .create(ItemsService::class.java)
            .getGetItemsAsync(myDetails.user_school)

        withContext(Dispatchers.IO) {
            try {
                val listResult = itemsService.await()
                database.itemsForSaleDao().upSert(listResult)
            } catch (e: Exception) {
                e.printStackTrace()
                _feedBack.postValue("network_error")
            }
        }
    }
    suspend fun addItems(courses: List<ItemsForSale>){
        withContext(Dispatchers.IO){
            try {
                database.itemsForSaleDao().upSert(courses)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

interface ItemsService {

    @GET("sell_item.php")
    fun getGetItemsAsync(
        @Query("school_id") school_id: String
    ): Deferred<List<ItemsForSale>>
}
