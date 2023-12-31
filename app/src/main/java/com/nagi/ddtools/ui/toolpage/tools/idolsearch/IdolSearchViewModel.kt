package com.nagi.ddtools.ui.toolpage.tools.idolsearch

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.nagi.ddtools.database.AppDatabase
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IdolSearchViewModel : ViewModel() {
    private val _idolGroupData = MutableLiveData<List<IdolGroupList>>()
    private val _locationData = MutableLiveData<Set<String>>()
    val idolGroupData: LiveData<List<IdolGroupList>> = _idolGroupData
    val locationData: LiveData<Set<String>> = _locationData
    fun loadIdolGroupData(jsonString: String, context: Context) {
        viewModelScope.launch {
            val idolGroupList = mutableListOf<IdolGroupList>()
            val locationList = mutableSetOf<String>()
            withContext(Dispatchers.IO) {
                jsonString.reader().use { reader ->
                    JsonReader(reader).use { jsonReader ->
                        jsonReader.beginArray()
                        while (jsonReader.hasNext()) {
                            var id = 0
                            var img_url = ""
                            var name = ""
                            var version = 0
                            var location = ""
                            var desc = ""
                            jsonReader.beginObject()
                            while (jsonReader.hasNext()) {
                                when (jsonReader.nextName()) {
                                    "id" -> if (jsonReader.peek() != JsonToken.NULL) id =
                                        jsonReader.nextInt()

                                    "img_url" -> if (jsonReader.peek() != JsonToken.NULL) img_url =
                                        jsonReader.nextString()

                                    "name" -> if (jsonReader.peek() != JsonToken.NULL) name =
                                        jsonReader.nextString()

                                    "version" -> if (jsonReader.peek() != JsonToken.NULL) version =
                                        jsonReader.nextInt()

                                    "location" -> if (jsonReader.peek() != JsonToken.NULL) location =
                                        jsonReader.nextString()

                                    "desc" -> if (jsonReader.peek() != JsonToken.NULL) desc =
                                        jsonReader.nextString()

                                    else -> jsonReader.skipValue()
                                }
                            }
                            jsonReader.endObject()
                            locationList.add(location)
                            idolGroupList.add(
                                IdolGroupList(id, img_url, name, version, location, desc)
                            )
                        }
                        jsonReader.endArray()
                    }
                }
                // 更新 LiveData
                _locationData.postValue(locationList)
                _idolGroupData.postValue(idolGroupList)
                withContext(Dispatchers.IO) {
                    AppDatabase.getInstance(context).idolGroupListDao().insertAll(idolGroupList)
                }
            }
        }
    }

    fun getIdolGroupListByLocation(location: String, context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _idolGroupData.postValue(
                    AppDatabase.getInstance(context).idolGroupListDao().getByLocation(location)
                )
            }
        }
    }
}
