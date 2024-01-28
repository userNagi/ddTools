package com.nagi.ddtools.ui.toolpage.tools.activitysearch.details

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import com.nagi.ddtools.data.TagsList
import com.nagi.ddtools.database.activityList.ActivityList
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import com.nagi.ddtools.databinding.ActivityDetailsActivityBinding
import com.nagi.ddtools.ui.adapter.IdolGroupListAdapter
import com.nagi.ddtools.ui.adapter.TagListAdapter
import com.nagi.ddtools.ui.base.DdToolsBindingBaseActivity
import com.nagi.ddtools.ui.minepage.user.login.LoginActivity
import com.nagi.ddtools.utils.FileUtils
import com.nagi.ddtools.utils.MapUtils
import com.nagi.ddtools.utils.UiUtils.dialog
import com.nagi.ddtools.utils.UiUtils.openPage
import com.nagi.ddtools.utils.UiUtils.toast

class ActivityDetailsActivity : DdToolsBindingBaseActivity<ActivityDetailsActivityBinding>() {
    private val viewModel: ActivityDetailsViewModel by viewModels()
    private lateinit var groupAdapter: IdolGroupListAdapter
    private var isAdapterInitialized = false
    private var id = 0
    override fun createBinding(): ActivityDetailsActivityBinding {
        return ActivityDetailsActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initView() {
        binding.apply {
            titleInclude.apply {
                titleBack.setOnClickListener { finish() }
                titleText.text = "活动详情"
            }
            detailsEvaluate.text = "评价"
        }
        initGroupAdapter()
    }

    private fun initViewModel() {
        id = intent.extras?.getInt("id") ?: 0
        viewModel.getUser()
        viewModel.setId(id)
        viewModel.getTags("activity", id.toString())
        updateTagAdapter(emptyList())
        viewModel.data.observe(this) {
            updateView(it)
        }
        viewModel.groupList.observe(this) { data ->
            upGroupAdapter(data)
        }
        viewModel.tags.observe(this) { data ->
            updateTagAdapter(data)
        }
    }

    private fun initGroupAdapter() {
        if (!isAdapterInitialized) {
            groupAdapter = IdolGroupListAdapter(mutableListOf())
        }
        binding.detailsGroupList.adapter = groupAdapter
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

    private fun updateTagAdapter(data: List<TagsList>) {
        binding.apply {
            if (data.isNotEmpty()) {
                detailsEvaluateList.visibility = View.VISIBLE
                detailsEvaluateList.adapter = TagListAdapter(data)
            } else {
                detailsEvaluateList.visibility = View.GONE
                detailsEvaluateInclude.text = "暂时还没有评价，点我添加一个，快来试试吧"
                detailsEvaluateInclude.setOnClickListener {
                    if (viewModel.users.value == null) {
                        dialog(
                            "提示",
                            "为了安全起见，添加/点赞暂只支持已登录用户使用，请先登录",
                            "去登录",
                            "我就看看",
                            { openPage(this@ActivityDetailsActivity, LoginActivity::class.java) }
                        )
                    } else {
                        val editText = EditText(applicationContext).apply {
                            gravity = Gravity.CENTER
                            hint = "请输入标签内容，最多十个字"
                            maxLines = 10
                        }
                        var content = ""
                        dialog(
                            title = "",
                            message = "",
                            positiveButtonText = "确定",
                            negativeButtonText = "取消",
                            onPositive = {
                                content = editText.text.toString()
                                if (content.isEmpty()) {
                                    toast("未填写内容")
                                } else {
                                    viewModel.addTags("activity", id, content)
                                }
                            },
                            onNegative = {},
                            customView = editText,
                            onDismiss = {
                                if (content.isEmpty()) {
                                    toast("未填写内容")
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun upGroupAdapter(data: List<IdolGroupList>) {
        groupAdapter.updateData(data)
    }
}