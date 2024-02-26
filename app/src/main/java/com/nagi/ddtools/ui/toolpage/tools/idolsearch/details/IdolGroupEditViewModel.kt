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
import com.nagi.ddtools.resourceGet.NetGet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IdolGroupEditViewModel : ViewModel() {
    private val _info = MutableLiveData<IdolGroupList>()
    val info: LiveData<IdolGroupList> = _info

    private val _mediaList = MutableLiveData<List<MediaList>>()
    val mediaList: LiveData<List<MediaList>> = _mediaList

    private val groupListDao: IdolGroupListDao by lazy {
        AppDatabase.getInstance().idolGroupListDao()
    }

    private val _submitResult = MutableLiveData<String?>()
    val submitResult: LiveData<String?> = _submitResult

    fun setId(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val infoData = groupListDao.getById(id)
                var mediaResult = mutableListOf<MediaList>()
                val mediaJson = JsonParser.parseString(infoData.ext).asJsonObject
                if (mediaJson.has("media")) {
                    val mediaList = mediaJson.get("media").asJsonArray
                    val itemType = object : TypeToken<List<MediaList>>() {}.type
                    mediaResult = Gson().fromJson(mediaList, itemType)
                }
                _info.postValue(infoData)
                _mediaList.postValue(mediaResult)
            }
        }
    }
}