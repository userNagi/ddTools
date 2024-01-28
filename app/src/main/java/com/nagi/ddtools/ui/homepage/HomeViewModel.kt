package com.nagi.ddtools.ui.homepage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nagi.ddtools.database.AppDatabase
import com.nagi.ddtools.database.homePagList.HomePageList
import com.nagi.ddtools.database.homePagList.HomePageListDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {
    private val database: HomePageListDao by lazy {
        AppDatabase.getInstance().homePageDao()
    }

    private val _homeListData = MutableLiveData<List<HomePageList>>()
    val homeListData: LiveData<List<HomePageList>> = _homeListData
    fun loadHomePageList() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _homeListData.postValue(database.getAll())
            }
        }
    }

    fun removeData(data: HomePageList) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.delete(data)
                loadHomePageList()
            }
        }
    }

    fun updateData(data: HomePageList) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.update(data)
            }
            loadHomePageList()
        }
    }

    fun addData(data: HomePageList) {
        val currentList = _homeListData.value?.toMutableList() ?: mutableListOf()
        currentList.add(data)
        _homeListData.postValue(currentList)
        setHomePageList(data)
    }

    private fun setHomePageList(currentList: HomePageList) {
        viewModelScope.launch(Dispatchers.IO) {
            database.insert(currentList)
        }
        loadHomePageList()
    }

}