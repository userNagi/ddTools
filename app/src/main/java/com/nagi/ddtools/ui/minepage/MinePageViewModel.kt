package com.nagi.ddtools.ui.minepage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.nagi.ddtools.database.AppDatabase
import com.nagi.ddtools.database.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MinePageViewModel : ViewModel() {
    private val database = AppDatabase.getInstance()
    private var _user = MutableLiveData<List<User>>()
    var user: LiveData<List<User>> = _user

    init {
        loadUsersFromDatabase()
    }

    fun reGetUser() {
        loadUsersFromDatabase()
    }

    private fun loadUsersFromDatabase() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val users = database.userDao().getAll()
                _user.postValue(users)
            }
        }

    }
}