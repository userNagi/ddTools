package com.nagi.ddtools.ui.minepage.user.login

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.core.view.WindowInsetsControllerCompat
import com.nagi.ddtools.R
import com.nagi.ddtools.databinding.ActivityLoginBinding
import com.nagi.ddtools.ui.base.DdToolsBaseActivity
import com.nagi.ddtools.ui.minepage.user.register.RegisterActivity
import com.nagi.ddtools.utils.UiUtils.dialog
import com.nagi.ddtools.utils.UiUtils.openPage
import com.nagi.ddtools.utils.UiUtils.toast

class LoginActivity : DdToolsBaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        initView()
        setupObservers()
    }

    private fun initView() {
        binding.apply {
            loginButton.setOnClickListener {
                viewModel.onLoginClicked(
                    loginUserName.text.toString(),
                    loginPassWord.text.toString()
                )
            }
            loginAgreeUser.setOnCheckedChangeListener { _, isChecked ->
                if (!isChecked) {
                    showLoginDialog()
                }
            }
            loginUserName.addTextChangedListener(LoginWatching {
                loginUserNameLayout.error = null
            })
            loginPassWord.addTextChangedListener(LoginWatching {
                loginPasswordLayout.error = null
            })
            registerButton.setOnClickListener {
                openPage(
                    this@LoginActivity,
                    RegisterActivity::class.java
                )
            }
        }
    }

    private fun setupObservers() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginViewModel.LoginState.Success -> {
                    applicationContext.toast("登录成功")
                    finish()
                }
                is LoginViewModel.LoginState.Error -> {
                    when (state.errorType) {
                        LoginViewModel.ErrorType.USERNAME -> binding.loginUserNameLayout.error =
                            state.errorMessage

                        LoginViewModel.ErrorType.PASSWORD -> binding.loginPasswordLayout.error =
                            state.errorMessage

                        LoginViewModel.ErrorType.AGREEMENT -> binding.loginUserNameLayout.error =
                            state.errorMessage

                        LoginViewModel.ErrorType.GENERAL -> binding.loginPasswordLayout.error =
                            state.errorMessage
                    }
                }
            }
        }
    }

    private fun showLoginDialog() {
        dialog(
            title = getString(R.string.login_dialog_title),
            message = getString(R.string.login_dialog_message),
            positiveButtonText = getString(R.string.confirm),
            negativeButtonText = getString(R.string.cancel),
            onPositive = {
                binding.loginAgreeUser.isChecked = true
            },
            onNegative = {
                binding.loginAgreeUser.isChecked = false
            }
        )
    }
    override fun setupStatusBar() {
        window.statusBarColor = Color.parseColor("#2766CCFF")
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
    }

    class LoginWatching(private val onTextChanged: (CharSequence?) -> Unit) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =
            onTextChanged.invoke(s)

        override fun afterTextChanged(s: Editable?) {}
    }

}