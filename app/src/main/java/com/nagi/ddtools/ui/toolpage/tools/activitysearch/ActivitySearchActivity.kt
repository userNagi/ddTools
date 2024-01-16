package com.nagi.ddtools.ui.toolpage.tools.activitysearch

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.nagi.ddtools.R
import com.nagi.ddtools.database.activityList.ActivityList
import com.nagi.ddtools.databinding.ActivityActivitySearchBinding
import com.nagi.ddtools.ui.adapter.ActivityListAdapter
import com.nagi.ddtools.ui.base.DdToolsBaseActivity
import com.nagi.ddtools.utils.FileUtils
import java.io.File

class ActivitySearchActivity : DdToolsBaseActivity() {
    private lateinit var binding: ActivityActivitySearchBinding
    private lateinit var adapter: ActivityListAdapter
    private var isAdapterInitialized = false
    private val viewModel: ActivitySearchViewModel by viewModels()
    private var loChoose = 0
    private var stChoose = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActivitySearchBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        initView()
        viewModel.activityListData.observe(this) { updateAdapter(it) }
    }

    private fun initView() {
        binding.searchTitleBack.setOnClickListener { finish() }
        binding.searchActivityActivity.setOnClickListener { updateActivityByStatus() }
        binding.searchLocation.setOnClickListener { updateActivityByLocation() }
        binding.searchDate.setOnClickListener { updateActivityByDate() }
        initAdapter()
    }

    private fun initAdapter() {
        val inputStream = File(applicationContext.filesDir, FileUtils.ACTIVITY_LIS_FILE)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        viewModel.loadActivityList(jsonString)
        if (!isAdapterInitialized) {
            adapter = ActivityListAdapter(mutableListOf())
        }
        binding.searchRecycler.adapter = adapter
        isAdapterInitialized = true
    }

    private fun updateActivityByLocation() {
        viewModel.locationListData.observe(this) { options ->
            val data = options.toList() as ArrayList<String>
            val builder = AlertDialog.Builder(this)
            data.add(0, resources.getText(R.string.search_location_choose).toString())
            builder.setTitle(resources.getText(R.string.please_choose).toString())
            builder.setSingleChoiceItems(
                data.toTypedArray(),
                loChoose
            ) { _, which ->
                loChoose = which
            }
            builder.setPositiveButton(getText(R.string.sure)) { _, _ ->
                binding.searchLocation.text = data[loChoose]
                viewModel.getActivityListByLocation(data[loChoose])
            }
            builder.setNegativeButton(getText(R.string.cancel)) { _, _ -> }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun updateActivityByStatus() {
        val dataList = arrayListOf("未开始", "进行中", "已结束")
        val builder = AlertDialog.Builder(this)
        dataList.add(0,"全部")
        builder.setTitle(resources.getText(R.string.please_choose).toString())
        builder.setSingleChoiceItems(
            dataList.toTypedArray(),
            stChoose
        ) { _, which ->
            stChoose = which
        }
        builder.setPositiveButton(getText(R.string.sure)) { _, _ ->
            binding.searchActivityActivity.text = dataList[stChoose]
            viewModel.getActivityListByStatus(dataList[stChoose])
        }
        builder.setNegativeButton(getText(R.string.cancel)) { _, _ -> }
        val dialog = builder.create()
        dialog.show()
    }

    private fun updateActivityByDate() {
        viewModel.dateListData.observe(this) {
            val dataList = it.toList() as ArrayList<String>
            val builder = AlertDialog.Builder(this)
            builder.setTitle(resources.getText(R.string.please_choose).toString())
            builder.setSingleChoiceItems(
                dataList.toTypedArray(),
                stChoose
            ) { _, which ->
                stChoose = which
            }
            builder.setPositiveButton(getText(R.string.sure)) { _, _ ->
                binding.searchDate.text = dataList[stChoose]
                viewModel.getActivityListByDate(dataList[stChoose])
            }
            builder.setNegativeButton(getText(R.string.cancel)) { _, _ -> }
            val dialog = builder.create()
            dialog.show()

        }
    }

    private fun updateAdapter(data: List<ActivityList>) {
        adapter.updateData(data)
    }
}