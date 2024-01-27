package com.nagi.ddtools.ui.base

import android.os.Bundle
import androidx.viewbinding.ViewBinding

/**
 * 封装binding
 */
abstract class DdToolsBindingBaseActivity<B : ViewBinding> : DdToolsBaseActivity() {
    lateinit var binding: B

    abstract fun createBinding(): B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = createBinding()
        setContentView(binding.root)
        setupStatusBar()
    }
}