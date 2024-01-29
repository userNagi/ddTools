package com.nagi.ddtools.ui.toolpage.tools.activitysearch.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nagi.ddtools.data.Resource
import com.nagi.ddtools.data.TagsList
import com.nagi.ddtools.database.AppDatabase
import com.nagi.ddtools.database.activityList.ActivityList
import com.nagi.ddtools.database.activityList.ActivityListDao
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import com.nagi.ddtools.database.idolGroupList.IdolGroupListDao
import com.nagi.ddtools.database.user.User
import com.nagi.ddtools.resourceGet.NetGet
import com.nagi.ddtools.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActivityDetailsViewModel : ViewModel() {

    private val _users = MutableLiveData<User>()
    val users: LiveData<User> = _users

    private val _data = MutableLiveData<ActivityList>()
    val data: LiveData<ActivityList> = _data

    private val _tags = MutableLiveData<List<TagsList>>()
    val tags: LiveData<List<TagsList>> = _tags

    private val _groupList = MutableLiveData<List<IdolGroupList>>()
    val groupList: LiveData<List<IdolGroupList>> = _groupList

    private val activityListDao: ActivityListDao by lazy {
        AppDatabase.getInstance().activityListDao()
    }
    private val groupListDao: IdolGroupListDao by lazy {
        AppDatabase.getInstance().idolGroupListDao()
    }

    fun setId(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val activityList = activityListDao.getById("$id")
                _data.postValue(activityList)
                val resultList = mutableListOf<IdolGroupList>()
                val groupIdListString = activityList.participatingGroup
                if (!groupIdListString.isNullOrEmpty()) {
                    val groupIdList = groupIdListString.split(",")
                    for (groupId in groupIdList) {
                        if (groupId.isNotEmpty()) {
                            groupListDao.getById(groupId.toInt()).let { group ->
                                resultList.add(group)
                            }
                        }
                    }
                    _groupList.postValue(resultList)
                }
            }
        }
    }

    fun getUser() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (AppDatabase.getInstance().userDao().getAll().isNotEmpty()) {
                    _users.postValue(AppDatabase.getInstance().userDao().getAll()[0])
                }
            }
        }
    }

    fun getTags(type: String, typeId: String) {
        NetGet.getTags(type, typeId) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val itemType = object : TypeToken<List<TagsList>>() {}.type
                    val tagList: List<TagsList> = Gson().fromJson(resource.data, itemType)

                    _tags.postValue(tagList)
                }

                is Resource.Error -> {
                    LogUtils.e("Error getting tags : + ${resource.message}")
                }
            }
        }
    }

    fun addTags(type: String, typeId: Int, content: String, callback: (String) -> Unit) {
        users.value?.let {
            NetGet.sendEvaluate(type, typeId, content, it.id) { result ->
                when (result) {
                    is Resource.Success -> callback(result.data)
                    is Resource.Error -> callback("error")
                }
            }
            getTags(type, typeId.toString())
        }
    }

}
