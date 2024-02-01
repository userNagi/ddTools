package com.nagi.ddtools.ui.minepage.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nagi.ddtools.database.AppDatabase
import com.nagi.ddtools.database.user.User
import kotlinx.coroutines.launch

class GlobalSettingViewModel : ViewModel() {

    private val _users = MutableLiveData<User>().apply {
        viewModelScope.launch {
            value = AppDatabase.getInstance().userDao().getAll().firstOrNull()
        }
    }
    val users: LiveData<User> = _users

    private val _location = MutableLiveData<List<String>>().apply {
        viewModelScope.launch {
//            value = AppDatabase.getInstance()
        }
    }
}