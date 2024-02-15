package com.nagi.ddtools.ui.toolpage.tools.activitysearch.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nagi.ddtools.database.AppDatabase
import com.nagi.ddtools.database.activityList.ActivityList
import com.nagi.ddtools.database.activityList.ActivityListDao
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import com.nagi.ddtools.database.idolGroupList.IdolGroupListDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditActivityViewModel : ViewModel() {
    private val _location = MutableLiveData<Set<String>>()
    val location: LiveData<Set<String>> = _location
    private val _groupList = MutableLiveData<List<IdolGroupList>>()
    val groupList: LiveData<List<IdolGroupList>> = _groupList

    private val _groupData = MutableLiveData<List<IdolGroupList>>()
    val groupData: LiveData<List<IdolGroupList>> = _groupData

    private val _data = MutableLiveData<ActivityList>()
    val data: LiveData<ActivityList> = _data

    private val groupListDao: IdolGroupListDao by lazy {
        AppDatabase.getInstance().idolGroupListDao()
    }
    private val activityListDao: ActivityListDao by lazy {
        AppDatabase.getInstance().activityListDao()
    }

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val groupList = groupListDao.getAll()
                _location.postValue(groupList.map { it.location }.toSet())
                _groupData.postValue(groupList)
            }
        }
    }

    fun setData(id: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val dataGet = activityListDao.getById(id)
                _data.postValue(dataGet)
                getGroup(dataGet.participatingGroup)
            }
        }
    }

    private fun getGroup(idAndTimes: String?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val groups = idAndTimes?.let { parseIdAndTimes(it) } ?: emptyList()
                _groupList.postValue(groups)
            }
        }
    }

    private fun parseIdAndTimes(idAndTimes: String): List<IdolGroupList> {
        return idAndTimes.split(',')
            .mapNotNull { parseIdAndTime(it) }
    }

    private fun parseIdAndTime(idAndTime: String): IdolGroupList? {
        val info = idAndTime.split('-')
        if (info.size < 3) return null

        return groupListDao.getById(info[0].toInt()).also {
            it.groupDesc = "${info[1]}-${info[2]}"
        }
    }


    fun addData(data: IdolGroupList) {
        val currentList = _groupList.value?.toMutableList() ?: mutableListOf()
        currentList.add(data)
        _groupList.postValue(currentList)
    }

    fun removeData(position: Int) {
        val currentList = _groupList.value!!.toMutableList()
        if (position >= 0 && position < currentList.size) {
            currentList.removeAt(position)
            _groupList.postValue(currentList)
        }
    }

    fun editData(data: IdolGroupList) {
        val currentList = _groupList.value?.toMutableList() ?: mutableListOf()
        currentList.firstOrNull { it.id == data.id }?.let { it.groupDesc = data.groupDesc }
        _groupList.postValue(currentList)
    }

}