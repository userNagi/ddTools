package com.nagi.ddtools.ui.toolpage.tools.idolsearch.details

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.nagi.ddtools.data.MediaList
import com.nagi.ddtools.data.Resource
import com.nagi.ddtools.data.TagsList
import com.nagi.ddtools.database.AppDatabase
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import com.nagi.ddtools.database.idolList.IdolList
import com.nagi.ddtools.database.user.User
import com.nagi.ddtools.resourceGet.NetGet
import com.nagi.ddtools.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IdolDetailsViewModel : ViewModel() {
    private val _users = MutableLiveData<User>()
    val users: LiveData<User> = _users

    private val _data = MutableLiveData<IdolList>()
    val data: LiveData<IdolList> = _data

    private val _tags = MutableLiveData<List<TagsList>>()
    val tags: LiveData<List<TagsList>> = _tags

    private val _medias = MutableLiveData<List<MediaList>>()
    val medias: LiveData<List<MediaList>> = _medias

    private val _group = MutableLiveData<IdolGroupList>()
    val group: LiveData<IdolGroupList> = _group

    // 这里不知道为啥，postValue会报data不能为null，虽然已经给他在外面判断了，但是还是报。
    // 而且他喵的我加!!还会告诉我没用，服啦，只能加个注解了(*_*)
    @SuppressLint("NullSafeMutableLiveData")
    fun setData(id: Int, dataGet:IdolList? =null) {
        if (dataGet != null) {
            loadGroup(dataGet.groupId)
            loadMediaData(dataGet.ext)
            _data.postValue(dataGet)
        } else {
            setDataById(id)
        }
    }

    private fun setDataById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val dataGet = AppDatabase.getInstance().idolListDao().getById(id)
            setData(id,dataGet)
            _data.postValue(dataGet)
        }
    }

    private fun loadGroup(id:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val groupData = AppDatabase.getInstance().idolGroupListDao().getById(id)
            _group.postValue(groupData)
        }
    }

    private fun loadMediaData(ext: String? = "") {
        viewModelScope.launch(Dispatchers.IO) {
            var mediaResult = mutableListOf<MediaList>()
            if (!ext.isNullOrEmpty()) {
                try {
                    val mediaJson = JsonParser.parseString(ext).asJsonObject
                    if (mediaJson.has("media")) {
                        val mediaList = mediaJson.get("media").asJsonArray
                        val itemType = object : TypeToken<List<MediaList>>() {}.type
                        mediaResult = Gson().fromJson(mediaList, itemType)
                    }
                    _medias.postValue(mediaResult)
                } catch (e: Exception) {
                    LogUtils.e("Error getting media : + ${e.message}")
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

    fun getTags(type: String, typeId: String, id: Int? = 0) {
        NetGet.getTags(type, typeId, id) { resource ->
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

    fun addTags(
        type: String,
        typeId: Int,
        content: String,
        action: String,
        tagId: String,
        callback: (String) -> Unit
    ) {
        users.value?.let {
            NetGet.sendEvaluate(type, typeId, content, it.id, action, tagId) { result ->
                when (result) {
                    is Resource.Success -> callback(result.data)
                    is Resource.Error -> callback("error")
                }
            }
        }
    }
}