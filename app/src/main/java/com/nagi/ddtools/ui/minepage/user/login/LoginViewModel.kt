package com.nagi.ddtools.ui.minepage.user.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nagi.ddtools.data.Resource
import com.nagi.ddtools.database.AppDatabase
import com.nagi.ddtools.database.user.User
import com.nagi.ddtools.resourceGet.NetGet
import com.nagi.ddtools.utils.DataUtils

class LoginViewModel : ViewModel() {
    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    fun onLoginClicked(username: String, password: String) {
        if (username.isEmpty()) {
            updateLoginState(LoginState.Error(ErrorType.USERNAME, "请输入用户名"))
            return
        }
        if (password.isEmpty()) {
            updateLoginState(LoginState.Error(ErrorType.PASSWORD, "请输入密码"))
            return
        }
        if (!DataUtils.checkInput(password) || !DataUtils.checkInputText(username)) {
            updateLoginState(LoginState.Error(ErrorType.PASSWORD, "输入需要数字字母且在6-18位之间"))
            return
        }
        doLoginRequest(username, password)
    }

    private fun updateUser(user: User) {
        AppDatabase.getInstance().userDao().insert(user)
    }

    private fun doLoginRequest(username: String, password: String) {
        NetGet.userLogin({ resource ->
            when (resource) {
                is Resource.Success -> {
                    val userInfo = resource.data
                    updateUser(userInfo)
                    _loginState.postValue(LoginState.Success)
                }

                is Resource.Error -> {
                    val errorMessage = resource.message
                    _loginState.postValue(LoginState.Error(ErrorType.GENERAL, errorMessage))
                }
            }
        }, username, password)

    }

    private fun updateLoginState(state: LoginState) {
        _loginState.value = state
    }

    sealed class LoginState {
        data object Success : LoginState()
        data class Error(val errorType: ErrorType, val errorMessage: String) : LoginState()
    }

    enum class ErrorType {
        USERNAME, PASSWORD, AGREEMENT, GENERAL
    }
}