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
import com.nagi.ddtools.database.idolList.IdolList
import com.nagi.ddtools.database.idolList.IdolListDao
import com.nagi.ddtools.utils.LogUtils

class IdolSearchViewModel : ViewModel() {
    private val _idolGroupData = MutableLiveData<List<IdolGroupList>>()
    private val _locationData = MutableLiveData<Map<String, Int>>()
    private val _idolListData = MutableLiveData<List<IdolList>>()
    private val groupDataBase: IdolGroupListDao by lazy {
        AppDatabase.getInstance().idolGroupListDao()
    }
    private val idolDataBase: IdolListDao by lazy {
        AppDatabase.getInstance().idolListDao()
    }

    val idolGroupData: LiveData<List<IdolGroupList>> = _idolGroupData
    val locationData: LiveData<Map<String, Int>> = _locationData
    val idolListData: LiveData<List<IdolList>> = _idolListData

    fun loadIdolGroupData(jsonString: String, lo: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val itemType = object : TypeToken<List<IdolGroupList>>() {}.type
                val idolGroupList: List<IdolGroupList> = Gson().fromJson(jsonString, itemType)
                val locationCountMap: Map<String, Int> =
                    idolGroupList.groupingBy { it.location }.eachCount()
                _locationData.postValue(locationCountMap)
                _idolGroupData.postValue(idolGroupList.filter { lo.isEmpty() || lo == it.location })
                groupDataBase.deleteAll()
                groupDataBase.insertAll(idolGroupList)
            }
        }
    }

    fun loadIdolListData(jsonString: String, lo: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val itemType = object : TypeToken<List<IdolList>>() {}.type
                    val idolList: List<IdolList> = Gson().fromJson(jsonString, itemType)
                    _idolListData.postValue(idolList.filter { lo.isEmpty() || lo == it.location })
                    idolDataBase.deleteAll()
                    idolDataBase.insertAll(idolList)
                } catch (e: Exception) {
                    LogUtils.e("$e")
                }
            }
        }
    }


    fun getIdolGroupListByLocation(lo: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                if (lo == "全世界") groupDataBase.getAll() else groupDataBase.getByLocation(lo)
            }
            _idolGroupData.postValue(result)
        }
    }

    fun getIdolListByLocation(lo: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                if (lo == "全世界") idolDataBase.getAll() else idolDataBase.getByLocation(lo)
            }
            _idolListData.postValue(result)
        }
    }
}
