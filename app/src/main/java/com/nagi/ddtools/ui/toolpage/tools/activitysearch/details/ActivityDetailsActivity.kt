package com.nagi.ddtools.ui.toolpage.tools.activitysearch.details

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.nagi.ddtools.database.activityList.ActivityList
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import com.nagi.ddtools.databinding.ActivityDetailsActivityBinding
import com.nagi.ddtools.ui.adapter.IdolGroupListAdapter
import com.nagi.ddtools.ui.base.DdToolsBindingBaseActivity
import com.nagi.ddtools.utils.MapUtils

class ActivityDetailsActivity : DdToolsBindingBaseActivity<ActivityDetailsActivityBinding>() {
    private val viewModel: ActivityDetailsViewModel by viewModels()
    private lateinit var adapter: IdolGroupListAdapter
    private var isAdapterInitialized = false
    override fun createBinding(): ActivityDetailsActivityBinding {
        return ActivityDetailsActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        viewModel.setId(intent.extras!!.getInt("id"))
        viewModel.data.observe(this) {
            updateView(it)
        }
        viewModel.groupList.observe(this) { data ->
            updateAdapter(data)
        }
    }

    private fun initView() {
        binding.apply {
            titleInclude.apply {
                titleBack.setOnClickListener { finish() }
                titleText.text = "活动详情"
            }
        }
        initAdapter()
    }

    private fun initAdapter() {
        if (!isAdapterInitialized) {
            adapter = IdolGroupListAdapter(mutableListOf())
        }
        binding.detailsGroupList.adapter = adapter
        isAdapterInitialized = true
    }

    private fun updateView(data: ActivityList) {
        binding.apply {
            detailsTitle.text = data.name
            detailsVideoLink.visibility = View.GONE
            detailsDateText.text = data.duration_date
            detailsTimeText.text = data.duration_time
            detailsLocationText.apply {
                text = data.location_desc
                setOnClickListener { MapUtils.chooseLocation(context, data.location_desc) }
            }
            if (data.bili_url?.isNotEmpty() == true) {
                detailsVideoLink.visibility = View.VISIBLE
            }
        }
    }

    private fun updateAdapter(data: List<IdolGroupList>) {
        adapter.updateData(data)
    }
}