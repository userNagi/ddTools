package com.nagi.ddtools.ui.toolpage.tools.activitysearch

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.nagi.ddtools.R
import com.nagi.ddtools.data.Resource
import com.nagi.ddtools.database.activityList.ActivityList
import com.nagi.ddtools.databinding.ActivityActivitySearchBinding
import com.nagi.ddtools.resourceGet.NetGet
import com.nagi.ddtools.ui.adapter.ActivityListAdapter
import com.nagi.ddtools.ui.base.DdToolsBaseActivity
import com.nagi.ddtools.utils.FileUtils
import com.nagi.ddtools.utils.LogUtils
import com.nagi.ddtools.utils.NetUtils
import com.nagi.ddtools.utils.UiUtils
import com.nagi.ddtools.utils.UiUtils.toast
import java.io.File

class ActivitySearchActivity : DdToolsBaseActivity() {
    private lateinit var binding: ActivityActivitySearchBinding
    private lateinit var adapter: ActivityListAdapter
    private var isAdapterInitialized = false
    private val viewModel: ActivitySearchViewModel by viewModels()
    private var loChoose = 0
    private var stChoose = 1
    private var daChoose = 0
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
        binding.searchResearch.setOnClickListener { reGetData() }
        binding.searchRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    binding.searchResearch.hide()
                } else if (dy < 0) {
                    binding.searchResearch.show()
                }
            }
        })
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
            val dataList = ArrayList(options).apply {
                add(0, getString(R.string.search_location_choose))
            }
            createSingleChoiceDialog(
                title = getString(R.string.please_choose),
                items = dataList,
                checkedItem = loChoose,
                onItemSelected = { choice ->
                    loChoose = choice
                    binding.searchLocation.text = dataList[choice]
                    viewModel.getActivityListByLocation(dataList[choice])
                }
            ).show()
        }
    }

    private fun updateActivityByStatus() {
        val dataList = arrayListOf("全部", "未开始", "进行中", "已结束")
        createSingleChoiceDialog(
            title = getString(R.string.please_choose),
            items = dataList,
            checkedItem = stChoose,
            onItemSelected = { choice ->
                stChoose = choice
                binding.searchActivityActivity.text = dataList[choice]
                viewModel.getActivityListByStatus(dataList[choice])
            }
        ).show()
    }

    private fun updateActivityByDate() {
        viewModel.dateListData.observe(this) { dates ->
            val dataList = ArrayList(dates)
            createSingleChoiceDialog(
                title = getString(R.string.please_choose),
                items = dataList,
                checkedItem = daChoose,
                onItemSelected = { choice ->
                    daChoose = choice
                    binding.searchDate.text = dataList[choice]
                    viewModel.getActivityListByDate(dataList[choice])
                }
            ).show()
        }
    }

    private fun createSingleChoiceDialog(
        title: String,
        items: List<String>,
        checkedItem: Int,
        onItemSelected: (Int) -> Unit
    ): AlertDialog {
        val builder = AlertDialog.Builder(this)
        with(builder) {
            setTitle(title)
            setSingleChoiceItems(items.toTypedArray(), checkedItem) { _, which ->
                onItemSelected(which)
            }
            setPositiveButton(getString(R.string.confirm)) { _, _ -> }
            setNegativeButton(getString(R.string.cancel)) { _, _ -> }
        }
        return builder.create()
    }
    private fun updateAdapter(data: List<ActivityList>) {
        adapter.updateData(data)
    }


    private var lastClickTime: Long = 0
    private val debounceTime: Long = 10000
    private fun reGetData() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= debounceTime) {
            lastClickTime = currentTime
            UiUtils.showLoading(this)

            try {
                NetUtils.fetchAndSave(
                    NetGet.getUrl("activity"), NetUtils.HttpMethod.POST,
                    emptyMap(), File(filesDir, FileUtils.ACTIVITY_LIS_FILE).path
                ) { resource ->
                    when (resource) {
                        is Resource.Success ->
                            runOnUiThread { initAdapter() }

                        is Resource.Error -> {
                            LogUtils.e("Failed to fetch idol group list: ${resource.message}")
                            toast("获取失败，请稍后重试")
                        }
                    }
                    UiUtils.hideLoading()
                }

            } catch (e: Exception) {
                LogUtils.e("Exception during fetching idol group list: ${e.message}")
                UiUtils.hideLoading()
            }
        } else {
            toast("请等待10秒后再尝试", Toast.LENGTH_LONG)
        }
    }

}