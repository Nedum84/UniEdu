package com.uniedu.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.uniedu.Event
import com.uniedu.model.*
import com.uniedu.repository.RepoItemForSaleCategory
import com.uniedu.room.DatabaseRoom
import com.uniedu.utils.ClassSharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class ModelItemForSaleCategory(application: Application) : AndroidViewModel(application) {

    private val database = DatabaseRoom.getDatabaseInstance(application)
    private val viewModelJob = SupervisorJob()//OR Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val itemCategoryRepo = RepoItemForSaleCategory(database)
    private val myDetails = ClassSharedPreferences(application).getCurUserDetail()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
    private val _queryString = MutableLiveData<Event<String>>()
    val queryString: LiveData<Event<String>> get() = _queryString
    init {
        viewModelScope.launch {
//            itemCategoryRepo.downloadItemCategories(myDetails)

            addToDb(ItemCategories.cats)//Load data
        }
        _queryString.value = Event("")
    }


    val feedBack = itemCategoryRepo.feedBack

    fun itemCategories(qString: String=""):LiveData<List<ItemCategory>>{
        return itemCategoryRepo.itemCats(qString)
    }

    //Item category clicking
    private var _curItemCategory = MutableLiveData<Event<ItemCategory>>()
    val curItemCategory:LiveData<Event<ItemCategory>> get() = _curItemCategory
    fun setItemCategory(item:ItemCategory){
        _curItemCategory.value = Event(item)
    }



    fun addToDb(item: List<ItemCategory>){
        viewModelScope.launch {
            itemCategoryRepo.addItemCat(item)
        }
    }









    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ModelItemForSaleCategory::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ModelItemForSaleCategory(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}







object ItemCategories{
    val cats = mutableListOf<ItemCategory>()

    init {
        cats.addAll(
            mutableListOf(
                ItemCategory(1,"Phone","",1),
                ItemCategory(2,"Laptop","",1),
                ItemCategory(3,"Tablet","",1),
                ItemCategory(4,"House/Hostel","",1),
                ItemCategory(5,"Snack","",1),
                ItemCategory(6,"Food Stuff","",1),
                ItemCategory(7,"Bag","",1),
                ItemCategory(8,"Textbook","",1),
                ItemCategory(9,"Cloth/Wear","",1),
                ItemCategory(10,"Electronics","",1),
                ItemCategory(11,"Accessories","",1),
                ItemCategory(12,"Accessories","",1),
                ItemCategory(13,"Others","",1)
            )
        )
    }
}
