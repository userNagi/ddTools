package com.nagi.ddtools.ui.minepage.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nagi.ddtools.database.AppDatabase
import com.nagi.ddtools.database.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GlobalSettingViewModel : ViewModel() {

    private val _users = MutableLiveData<User?>()
    val users: LiveData<User?> = _users

    private val _location = MutableLiveData<List<String>>()
    val location: LiveData<List<String>> = _location

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val databaseUser =
                    AppDatabase.getInstance().userDao().getAll().firstOrNull()
                val distinctLocations =
                    AppDatabase.getInstance().idolGroupListDao().getAll().map { it.location }
                        .distinct()
                databaseUser.let { _users.postValue(it) }
                _location.postValue(distinctLocations)
            }
        }
    }

}