package com.nagi.ddtools.ui.toolpage.tools.idolsearch.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.nagi.ddtools.data.MediaList
import com.nagi.ddtools.data.Resource
import com.nagi.ddtools.database.AppDatabase
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import com.nagi.ddtools.database.idolGroupList.IdolGroupListDao
import com.nagi.ddtools.database.idolList.IdolList
import com.nagi.ddtools.database.idolList.IdolTag
import com.nagi.ddtools.resourceGet.NetGet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IdolEditViewModel : ViewModel() {

    private val _info = MutableLiveData<IdolList>()
    val info: LiveData<IdolList> = _info

    private val _groupInfo = MutableLiveData<List<IdolGroupList>>()
    val groupInfo: LiveData<List<IdolGroupList>> = _groupInfo

    private val _groupLocation = MutableLiveData<Set<String>>()
    val groupLocation: LiveData<Set<String>> = _groupLocation

    private val _tagData = MutableLiveData<List<IdolTag>>()
    val tagData: LiveData<List<IdolTag>> = _tagData

    private val _mediaList = MutableLiveData<List<MediaList>>()
    val mediaList: LiveData<List<MediaList>> = _mediaList

    private val _submitResult = MutableLiveData<String?>()
    val submitResult: LiveData<String?> = _submitResult

    private val groupDao: IdolGroupListDao by lazy {
        AppDatabase.getInstance().idolGroupListDao()
    }

    init {
        NetGet.getIdolTag { resource ->
            when (resource) {
                is Resource.Success -> {
                    val data = JsonParser.parseString(resource.data).asJsonArray
                    val itemType = object : TypeToken<List<IdolTag>>() {}.type
                    val tagList: MutableList<IdolTag> = Gson().fromJson(data, itemType)
                    _tagData.postValue(tagList)
                }

                is Resource.Error -> {}
            }

            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val groupList = groupDao.getAll()
                    _groupLocation.postValue(groupList.map { it.location }.toSet())
                    _groupInfo.postValue(groupList)
                }
            }
        }
    }

    fun setData(data: IdolList) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                setMediaData(data.ext)
            }
        }
        _info.postValue(data)
    }

    fun setTag(tag: IdolTag) {
        _info.value?.let {
            val currentTag = it.copy(tag = tag)
            _info.postValue(currentTag)
        }
    }

    private fun setMediaData(ext: String?) {
        if (ext.isNullOrEmpty()) return
        val extJson = JsonParser.parseString(ext).asJsonObject
        var mediaResult = mutableListOf<MediaList>()
        if (extJson.has("media")) {
            val mediaList = extJson.get("media").asJsonArray
            val itemType = object : TypeToken<List<MediaList>>() {}.type
            mediaResult = Gson().fromJson(mediaList, itemType)
        }
        _mediaList.postValue(mediaResult)
    }

}