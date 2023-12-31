package com.nagi.ddtools.ui.toolpage.tools.idolsearch

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.nagi.ddtools.R
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import com.nagi.ddtools.databinding.ActivityIdolSearchBinding
import com.nagi.ddtools.ui.adapter.IdolGroupListAdapter


class IdolSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIdolSearchBinding
    private lateinit var adapter: IdolGroupListAdapter
    private var isAdapterInitialized = false
    private val viewModel: IdolSearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIdolSearchBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        initView()
        setupStatusBar()
        viewModel.idolGroupData.observe(this) { data -> updateAdapter(data) }
        updateIdolGroupData()
    }

    private fun initView() {
        binding.searchTitleBack.setOnClickListener { finish() }
        binding.searchLocation.setOnClickListener { updateIdolGroupData() }
        binding.searchSwitchSearch.setOnCheckedChangeListener { _, isChecked ->
            updateSwitchColors(isChecked)
        }
        initAdapter()
    }

    private fun initAdapter() {
        if (!isAdapterInitialized) {
            adapter = IdolGroupListAdapter(mutableListOf())
        }
        binding.searchRecycler.adapter = adapter
        isAdapterInitialized = true
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

    private fun updateIdolGroupData() {
        val inputStream = resources.openRawResource(R.raw.idolgrouplist)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        viewModel.loadIdolGroupData(jsonString, applicationContext)
    }

    private fun updateAdapter(data: List<IdolGroupList>) {
        adapter.updateData(data)
    }

    private fun setupStatusBar() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.lty)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
    }
}