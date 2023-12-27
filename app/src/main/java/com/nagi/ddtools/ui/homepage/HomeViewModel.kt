package com.nagi.ddtools.ui.homepage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.nagi.ddtools.database.AppDatabase
import com.nagi.ddtools.database.homePagLis.HomePageList
import kotlinx.coroutines.Dispatchers

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val database: AppDatabase = AppDatabase.getInstance(application)

    val homeListData: LiveData<List<HomePageList>> =
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            val data = database.homePageDao().getAll()
            emit(data)
        }
}