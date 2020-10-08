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

class RepoItemForSaleCategory(private val database: DatabaseRoom) {


    fun itemCats(searchQuery: String?=""):LiveData<List<ItemCategory>>{
        return if (searchQuery.isNullOrEmpty())
            database.itemCategoryDao.getAll()
        else
            database.itemCategoryDao.getAll("%$searchQuery%")
    }

    val feedBack:LiveData<String> get() = _feedBack
    private val _feedBack  = MutableLiveData<String>().apply {
        value = "success"
    }

    suspend fun downloadItemCategories(myDetails: MyDetails){
        val courseService = RetrofitConstantGET.retrofit
            .create(ItemCategoryService::class.java)
            .downloadAsync(myDetails.user_school)

        withContext(Dispatchers.IO) {
            try {
                val listResult = courseService.await()
                database.itemCategoryDao.upSert(listResult)
            } catch (e: Exception) {
                e.printStackTrace()
                _feedBack.postValue("network_error")
            }
        }
    }
    suspend fun addItemCat(items: List<ItemCategory>){
        withContext(Dispatchers.IO){
            try {
                database.itemCategoryDao.upSert(items)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

interface ItemCategoryService {

    @GET("add_item_category.php")
    fun downloadAsync(
        @Query("school_id") school_id: String
    ): Deferred<List<ItemCategory>>
}
