package com.nagi.ddtools.ui.minepage.user.userInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nagi.ddtools.database.AppDatabase
import com.nagi.ddtools.database.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserInfoViewModel : ViewModel() {
    private val database = AppDatabase.getInstance()
    private var _user = MutableLiveData<List<User>>()
    var user: LiveData<List<User>> = _user

    private var _thisUser = MutableLiveData<User>()
    var thisUser: LiveData<User> = _thisUser

    fun getUser() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val users = database.userDao().getAll()
                _user.postValue(users)
                _thisUser.postValue(users[0])
            }
        }
    }

    fun getUserLogin(): Boolean = !_user.value.isNullOrEmpty()

}