package com.nagi.ddtools.ui.toolpage.tools.idolsearch

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.nagi.ddtools.R
import com.nagi.ddtools.databinding.ActivityIdolSearchBinding

class IdolSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIdolSearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIdolSearchBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        initView()
        setupStatusBar()
    }

    private fun initView() {
        binding.searchTitleBack.setOnClickListener { finish() }
        binding.searchLocation.setOnClickListener { }
        binding.searchSwitchSearch.setOnCheckedChangeListener { _, isChecked ->
            updateSwitchColors(isChecked)
        }
    }

    private fun updateSwitchColors(isChecked: Boolean) {
        if (isChecked) {
            binding.searchSwitchTextLeft.setTextColor(Color.BLACK)
            binding.searchSwitchTextRight.setTextColor(Color.parseColor("#66CCFF"))
        } else {
            binding.searchSwitchTextLeft.setTextColor(Color.parseColor("#66CCFF"))
            binding.searchSwitchTextRight.setTextColor(Color.BLACK)
        }
    }

    private fun updateLocation() {

    }

    private fun setupStatusBar() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.lty)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
    }
}