package com.nagi.ddtools.ui.toolpage.tools.idolsearch

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.nagi.ddtools.R
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import com.nagi.ddtools.databinding.ActivityIdolSearchBinding
import com.nagi.ddtools.ui.adapter.IdolGroupListAdapter
import com.nagi.ddtools.utils.FileUtils
import java.io.File


class IdolSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIdolSearchBinding
    private lateinit var adapter: IdolGroupListAdapter
    private var isAdapterInitialized = false
    private var chooseWhich = 0
    private val viewModel: IdolSearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIdolSearchBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        initView()
        setupStatusBar()
        viewModel.idolGroupData.observe(this) { data -> updateAdapter(data) }
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
        val inputStream = File(applicationContext.filesDir, FileUtils.IDOL_GROUP_FILE)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        viewModel.loadIdolGroupData(jsonString)
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
        viewModel.locationData.observe(this) { options ->
            val data = options.toList() as ArrayList<String>
            val builder = AlertDialog.Builder(this)
            data.add(0,"全世界")
            builder.setTitle("请选择一个选项")
            builder.setSingleChoiceItems(
                data.toTypedArray(),
                chooseWhich
            ) { _, which ->
                chooseWhich = which
            }
            builder.setPositiveButton("确定") { _, _ ->
                binding.searchLocation.text = data[chooseWhich]
                viewModel.getIdolGroupListByLocation(data[chooseWhich])
            }
            builder.setNegativeButton("取消") { _, _ -> }
            val dialog = builder.create()
            dialog.show()
        }


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