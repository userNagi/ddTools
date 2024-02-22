package com.nagi.ddtools.ui.toolpage.tools.idolsearch

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.nagi.ddtools.R
import com.nagi.ddtools.data.Resource
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import com.nagi.ddtools.database.idolList.IdolList
import com.nagi.ddtools.databinding.ActivityIdolSearchBinding
import com.nagi.ddtools.resourceGet.NetGet.getUrl
import com.nagi.ddtools.ui.adapter.IdolGroupListAdapter
import com.nagi.ddtools.ui.adapter.IdolListAdapter
import com.nagi.ddtools.ui.base.DdToolsBaseActivity
import com.nagi.ddtools.ui.toolpage.tools.idolsearch.details.IdolEditActivity
import com.nagi.ddtools.ui.toolpage.tools.idolsearch.details.IdolGroupEditActivity
import com.nagi.ddtools.utils.FileUtils
import com.nagi.ddtools.utils.LogUtils
import com.nagi.ddtools.utils.NetUtils
import com.nagi.ddtools.utils.PrefsUtils
import com.nagi.ddtools.utils.UiUtils
import com.nagi.ddtools.utils.UiUtils.openPage
import com.nagi.ddtools.utils.UiUtils.toast
import java.io.File


class IdolSearchActivity : DdToolsBaseActivity() {
    private lateinit var binding: ActivityIdolSearchBinding
    private lateinit var groupListAdapter: IdolGroupListAdapter
    private lateinit var idolListAdapter: IdolListAdapter
    private var isAdapterInitialized = false
    private var chooseWhich = 0
    private var location: String = ""
    private val viewModel: IdolSearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIdolSearchBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        initView()
        viewModel.idolGroupData.observe(this) { data -> updateGroupAdapter(data) }
        viewModel.idolListData.observe(this) { data -> updateIdolAdapter(data) }
    }

    private fun initView() {
        location = PrefsUtils.getSettingLocation(applicationContext) ?: ""
        binding.searchLocation.text = location.ifEmpty { "全世界" }
        binding.searchTitleBack.setOnClickListener { finish() }
        binding.searchResearch.setOnClickListener { reGetData() }
        binding.searchLocation.setOnClickListener { updateIdolGroupData() }
        binding.searchSwitchSearch.setOnCheckedChangeListener { _, s -> updateSwitch(s) }
        binding.searchAdd.setOnClickListener {
            openPage(
                this@IdolSearchActivity,
                if (binding.searchSwitchSearch.isChecked) IdolEditActivity::class.java
                else IdolGroupEditActivity::class.java
            )
        }
        binding.searchRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    binding.searchAdd.hide()
                    binding.searchResearch.hide()
                } else if (dy < 0) {
                    binding.searchAdd.show()
                    binding.searchResearch.show()
                }
            }
        })
        initAdapter()
    }

    private fun initAdapter() {
        val groupPath = File(applicationContext.filesDir, FileUtils.IDOL_GROUP_FILE)
        val idolPath = File(applicationContext.filesDir, FileUtils.IDOL_LIST_FILE)
        val groupString = groupPath.bufferedReader().use { it.readText() }
        val idolString = idolPath.bufferedReader().use { it.readText() }
        if (!isAdapterInitialized) {
            viewModel.loadIdolGroupData(groupString, location)
            viewModel.loadIdolListData(idolString, location)
            groupListAdapter = IdolGroupListAdapter(mutableListOf())
            idolListAdapter = IdolListAdapter(mutableListOf())
        }
        binding.searchRecycler.adapter = groupListAdapter
        isAdapterInitialized = true
    }

    private fun updateSwitch(isChecked: Boolean) {
        if (isChecked) {
            binding.searchSwitchTextLeft.setTextColor(Color.BLACK)
            binding.searchSwitchTextRight.setTextColor(resources.getColor(R.color.lty, null))
            binding.searchRecycler.adapter = idolListAdapter
        } else {
            binding.searchSwitchTextLeft.setTextColor(resources.getColor(R.color.lty, null))
            binding.searchSwitchTextRight.setTextColor(Color.BLACK)
            binding.searchRecycler.adapter = groupListAdapter
        }
    }

    private fun updateIdolGroupData() {
        if (viewModel.locationData.value?.isNotEmpty() == true) {
            val dataMap = viewModel.locationData.value?.toMap()
            val data = ArrayList<String>()
            val location = ArrayList<String>()
            dataMap?.forEach { (key, value) ->
                data.add("$key($value)")
                location.add(key)
            }
            val builder = AlertDialog.Builder(this)
            data.add(0, resources.getText(R.string.search_location_choose).toString())
            location.add(0, resources.getText(R.string.search_location_choose).toString())
            builder.setTitle(resources.getText(R.string.please_choose).toString())
            builder.setSingleChoiceItems(
                data.toTypedArray(),
                chooseWhich
            ) { _, which ->
                chooseWhich = which
            }
            builder.setPositiveButton(getText(R.string.confirm)) { _, _ ->
                binding.searchLocation.text = data[chooseWhich]
                viewModel.getIdolGroupListByLocation(location[chooseWhich])
            }
            builder.setNegativeButton(getText(R.string.cancel)) { _, _ -> }
            val dialog = builder.create()
            dialog.show()

        }
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
                    getUrl("group"), NetUtils.HttpMethod.POST,
                    emptyMap(), File(filesDir, FileUtils.IDOL_GROUP_FILE).path
                ) { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            runOnUiThread { initAdapter() }
                        }

                        is Resource.Error -> {
                            LogUtils.e("Failed to fetch idol group list: ${resource.message}")
                            toast("获取失败，请稍后重试")
                        }
                    }
                    UiUtils.hideLoading()
                }
                chooseWhich = 0
                binding.searchLocation.text = "全世界"
            } catch (e: Exception) {
                LogUtils.e("Exception during fetching idol group list: ${e.message}")
                UiUtils.hideLoading()
            }
        } else {
            toast("请等待10秒后再尝试", Toast.LENGTH_LONG)
        }
    }

    private fun updateGroupAdapter(data: List<IdolGroupList>) {
        groupListAdapter.updateData(data)
    }

    private fun updateIdolAdapter(data: List<IdolList>) {
        idolListAdapter.updateData(data)
    }
}