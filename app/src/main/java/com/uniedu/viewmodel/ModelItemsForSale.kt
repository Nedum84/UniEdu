package com.uniedu.viewmodel

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.uniedu.Event
import com.uniedu.model.*
import com.uniedu.repository.RepoAnswersFrag
import com.uniedu.repository.RepoCourses
import com.uniedu.repository.RepoEBooks
import com.uniedu.repository.RepoItemForSale
import com.uniedu.room.DatabaseRoom
import com.uniedu.utils.ClassAlertDialog
import com.uniedu.utils.ClassSharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.*

class ModelItemsForSale(application: Application) : AndroidViewModel(application) {

    private val database = DatabaseRoom.getDatabaseInstance(application)
    private val viewModelJob = SupervisorJob()//OR Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val itemsRepo = RepoItemForSale(database)
    private val myDetails = ClassSharedPreferences(application).getCurUserDetail()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    val queryString: LiveData<Event<String>> get() = _queryString
    private val _queryString = MutableLiveData<Event<String>>()
    init {
        viewModelScope.launch {
            itemsRepo.getItems(myDetails)
        }

        _queryString.value = Event("")
    }

    val feedBack = itemsRepo.feedBack



    fun setSearchQuery(queryString: String=""){
        val q = queryString.toLowerCase(Locale.ROOT).replace(" ","")
        ClassSharedPreferences(getApplication()).setSearchQuery(q)

        _queryString.value = Event(q)
    }
    fun items(qString: String=""):LiveData<List<ItemsForSale>>{
        return itemsRepo.itemForSale(qString)
    }



    fun addToDb(item: List<ItemsForSale>){
        viewModelScope.launch {
            itemsRepo.addItems(item)
        }
    }


    //
//    Current Item for preview

    val curItem: LiveData<ItemsForSale> get() = _curItem
    private val _curItem = MutableLiveData<ItemsForSale>()
    fun setCurItem(item:ItemsForSale){
        _curItem.value = item
    }
    //
//    is it my item VISIBILITY
    fun isMyItem() = if (curItem.value!!.item_uploaded_by.toInt()== myDetails.user_id) View.VISIBLE else View.GONE









    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ModelItemsForSale::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ModelItemsForSale(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
