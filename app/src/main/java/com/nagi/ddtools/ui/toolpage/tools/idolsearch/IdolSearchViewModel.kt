package com.nagi.ddtools.ui.toolpage.tools.idolsearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nagi.ddtools.database.AppDatabase
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nagi.ddtools.database.idolGroupList.IdolGroupListDao

class IdolSearchViewModel : ViewModel() {
    private val _idolGroupData = MutableLiveData<List<IdolGroupList>>()
    private val _locationData = MutableLiveData<Set<String>>()
    private val database: IdolGroupListDao by lazy { AppDatabase.getInstance()!!.idolGroupListDao() }

    val idolGroupData: LiveData<List<IdolGroupList>> = _idolGroupData
    val locationData: LiveData<Set<String>> = _locationData

    fun loadIdolGroupData(jsonString: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val itemType = object : TypeToken<List<IdolGroupList>>() {}.type
                val idolGroupList: List<IdolGroupList> = Gson().fromJson(jsonString, itemType)
                val locationList: Set<String> = idolGroupList.map { it.location }.toSet()

                _locationData.postValue(locationList)
                _idolGroupData.postValue(idolGroupList)

                database.insertAll(idolGroupList)
            }
        }
    }

    fun getIdolGroupListByLocation(lo: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                if (lo == "全世界") database.getAll() else database.getByLocation(lo)
            }
            _idolGroupData.postValue(result)
        }
    }
}
