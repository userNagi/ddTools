package com.nagi.ddtools.ui.minepage.user.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nagi.ddtools.data.Resource
import com.nagi.ddtools.resourceGet.NetGet

class RegisterViewModel : ViewModel() {
    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState
    fun onRegisterClick(
        userName: String,
        password: String,
        passwordAgain: String,
        email: String,
        nickName: String
    ) {
        if (userName.isEmpty()) {
            updateRegisterState(RegisterState.Error(ErrorType.USERNAME, "请输入用户名"))
            return
        }
        if (password.isEmpty()) {
            updateRegisterState(RegisterState.Error(ErrorType.PASSWORD, "请输入密码"))
            return
        }
        if (passwordAgain.isEmpty()) {
            updateRegisterState(RegisterState.Error(ErrorType.PASSWORD_AGAIN, "请再次输入密码"))
            return
        }
        if (email.isEmpty()) {
            updateRegisterState(RegisterState.Error(ErrorType.EMAIL, "请输入邮箱"))
            return
        }
        if (nickName.isEmpty()) {
            updateRegisterState(RegisterState.Error(ErrorType.NICKNAME, "请输入昵称"))
            return
        }
        if (password != passwordAgain) {
            updateRegisterState(
                RegisterState.Error(
                    ErrorType.PASSWORD_AGAIN,
                    "两次输入的密码不一致"
                )
            )
            return
        }

        doRegisterRequest(userName, password, email, nickName)
    }

    private fun updateRegisterState(state: RegisterState) {
        _registerState.value = state
    }

    private fun doRegisterRequest(
        userName: String,
        password: String,
        email: String,
        nickName: String
    ) {
        NetGet.userRegister(
            { resource ->
                when (resource) {
                    is Resource.Success -> {
                        if (resource.data.isNotEmpty()) {
                            _registerState.postValue(RegisterState.Success)
                        }
                    }

                    is Resource.Error -> {
                        val errorMessage = resource.message
                        _registerState.postValue(
                            RegisterState.Error(ErrorType.GENERAL, errorMessage)
                        )
                    }
                }
            }, userName, password, email, nickName
        )
    }


    sealed class RegisterState {
        data object Success : RegisterState()
        data class Error(val errorType: ErrorType, val errorMessage: String) : RegisterState()
    }

    enum class ErrorType {
        USERNAME, PASSWORD, EMAIL, NICKNAME, GENERAL, PASSWORD_AGAIN
    }
}