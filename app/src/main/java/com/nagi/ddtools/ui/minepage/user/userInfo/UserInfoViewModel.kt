package com.nagi.ddtools.ui.minepage.user.userInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nagi.ddtools.database.AppDatabase
import com.nagi.ddtools.database.activityList.ActivityList
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

    private var _collectActivity = MutableLiveData<List<ActivityList>>()
    var collectActivity: LiveData<List<ActivityList>> = _collectActivity

    fun getUser() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val users = database.userDao().getAll()
                _user.postValue(users)
                _thisUser.postValue(users[0])
                getUserCollectActivity(users[0].id)
            }
        }
    }

    fun getUserLogin(): Boolean = !_user.value.isNullOrEmpty()
    fun logout() {
        viewModelScope.launch { withContext(Dispatchers.IO) { database.userDao().deleteAll() } }
    }

    private fun getUserCollectActivity(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val activities = mutableListOf<ActivityList>()
                val localCollects = database.localCollectDao().getByUserId(id)
                localCollects.forEach {
                    if (it.collectType == "activity") {
                        val item = database.activityListDao().getById("${it.collectId}")
                        activities.add(item)
                    }
                }
                _collectActivity.postValue(activities)
            }
        }
    }


}