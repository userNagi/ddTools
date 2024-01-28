package com.nagi.ddtools.ui.minepage.user.register

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.core.view.WindowInsetsControllerCompat
import com.nagi.ddtools.R
import com.nagi.ddtools.databinding.ActivityRegisterBinding
import com.nagi.ddtools.ui.base.DdToolsBaseActivity
import com.nagi.ddtools.utils.UiUtils.dialog
import com.nagi.ddtools.utils.UiUtils.hideLoading
import com.nagi.ddtools.utils.UiUtils.showLoading
import com.nagi.ddtools.utils.UiUtils.toast

class RegisterActivity : DdToolsBaseActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        initView()
        setupObservers()
    }

    private fun initView() {
        binding.apply {
            registerTitleBack.setOnClickListener { finish() }
            registerButton.setOnClickListener {
                showLoading(applicationContext)
                viewModel.onRegisterClick(
                    userName = registerUserName.text.toString(),
                    password = registerPassWord.text.toString(),
                    passwordAgain = registerPassWordAgain.text.toString(),
                    email = registerEmailEditText.text.toString(),
                    nickName = registerNicknameEditText.text.toString()
                )
            }
            registerAgreeUser.setOnCheckedChangeListener { _, isChecked ->
                if (!isChecked) {
                    showLoginDialog()
                }
            }
            registerUserName.addTextChangedListener(RegisterWatching {
                registerUserNameLayout.error = null
            })
            registerPassWord.addTextChangedListener(RegisterWatching {
                registerPasswordLayout.error = null
            })
            registerPassWordAgain.addTextChangedListener(RegisterWatching {
                registerPasswordAgainLayout.error = null
            })
            registerEmailEditText.addTextChangedListener(RegisterWatching {
                registerEmailLayout.error = null
            })
            registerNicknameEditText.addTextChangedListener(RegisterWatching {
                registerNicknameLayout.error = null
            })
        }
    }

    private fun setupObservers() {
        viewModel.registerState.observe(this) { state ->
            hideLoading()
            when (state) {
                is RegisterViewModel.RegisterState.Success -> {
                    toast("注册成功")
                    finish()
                }

                is RegisterViewModel.RegisterState.Error -> {
                    binding.apply {
                        when (state.errorType) {
                            RegisterViewModel.ErrorType.PASSWORD ->
                                registerPasswordLayout.error = state.errorMessage

                            RegisterViewModel.ErrorType.GENERAL ->
                                registerPasswordLayout.error = state.errorMessage

                            RegisterViewModel.ErrorType.EMAIL ->
                                registerEmailLayout.error = state.errorMessage

                            RegisterViewModel.ErrorType.NICKNAME ->
                                registerNicknameLayout.error = state.errorMessage

                            RegisterViewModel.ErrorType.USERNAME ->
                                registerUserName.error = state.errorMessage

                            RegisterViewModel.ErrorType.PASSWORD_AGAIN ->
                                registerPasswordAgainLayout.error = state.errorMessage
                        }
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
                binding.registerAgreeUser.isChecked = true
            },
            onNegative = {
                binding.registerAgreeUser.isChecked = false
            }
        )
    }

    override fun setupStatusBar() {
        window.statusBarColor = Color.parseColor("#2766CCFF")
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
    }

    class RegisterWatching(private val onTextChanged: (CharSequence?) -> Unit) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged.invoke(s)
        }
    }
}