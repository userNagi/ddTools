package com.nagi.ddtools.ui.toolpage.tools.idolsearch

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.nagi.ddtools.R
import com.nagi.ddtools.databinding.ActivityIdolSearchBinding
import java.io.BufferedReader
import java.io.InputStreamReader


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
        binding.searchLocation.setOnClickListener { updateLocation() }
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
        val `is` = resources.openRawResource(R.raw.idolgrouplist)
        val reader = BufferedReader(InputStreamReader(`is`))
        var line: String?
        val json = StringBuilder()
        while (reader.readLine().also { line = it } != null) {
            json.append(line)
        }
    }

    private fun setupStatusBar() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.lty)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
    }
}