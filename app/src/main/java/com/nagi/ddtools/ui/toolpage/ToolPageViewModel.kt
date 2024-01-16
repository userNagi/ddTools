package com.nagi.ddtools.ui.toolpage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nagi.ddtools.utils.PrefsUtils

class ToolPageViewModel(application: Application) : AndroidViewModel(application) {
    private val _title = MutableLiveData<String>().apply {
        value = PrefsUtils.getToolTitle(application)
    }

    fun setTitle(title: String) {
        try {
            _title.value = title
        } catch (e: Exception) {
            _title.postValue(title)
        }
        PrefsUtils.setToolTitle(getApplication(), title)
    }

    val title: LiveData<String> = _title
}