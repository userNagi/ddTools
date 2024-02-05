package com.nagi.ddtools.ui.toolpage.tools.idolsearch.details

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import android.view.Gravity
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.nagi.ddtools.data.TagsList
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import com.nagi.ddtools.databinding.ActivityIdolDetailsBinding
import com.nagi.ddtools.ui.adapter.ActivityListAdapter
import com.nagi.ddtools.ui.adapter.IdolGroupListAdapter
import com.nagi.ddtools.ui.adapter.IdolMediaListAdapter
import com.nagi.ddtools.ui.adapter.TagListAdapter
import com.nagi.ddtools.ui.base.DdToolsBindingBaseActivity
import com.nagi.ddtools.ui.minepage.user.login.LoginActivity
import com.nagi.ddtools.utils.UiUtils
import com.nagi.ddtools.utils.UiUtils.dialog
import com.nagi.ddtools.utils.UiUtils.toast

class IdolDetailsActivity : DdToolsBindingBaseActivity<ActivityIdolDetailsBinding>() {
    private val viewModel: IdolDetailsViewModel by viewModels()
    private var id: Int = 0
    private lateinit var mediaAdapter: IdolMediaListAdapter
    private lateinit var idolAdapter: IdolGroupListAdapter
    private lateinit var activityAdapter: ActivityListAdapter
    override fun createBinding(): ActivityIdolDetailsBinding {
        return ActivityIdolDetailsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUser()
    }

    private fun initView() {
        binding.apply {
            titleInclude.apply {
                titleBack.setOnClickListener { finish() }
                titleText.text = "组合详情"
            }
            val layoutManager = FlexboxLayoutManager(applicationContext).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.FLEX_START
            }
            detailsEvaluateList.layoutManager = layoutManager
        }
        initActivityAdapter()
        initMediaAdapter()
        initIdolAdapter()
    }

    private fun initViewModel() {
        id = intent.extras?.getInt("id") ?: 0
        viewModel.setId(id)
        viewModel.getTags("group", id.toString())
        updateTagAdapter(emptyList())
        viewModel.data.observe(this) {
            updateView(it)
        }
        viewModel.medias.observe(this) {
            mediaAdapter.updateDate(it)
        }
        viewModel.memberList.observe(this) {
            idolAdapter.updateData(it)
        }
        viewModel.activityList.observe(this) {
            activityAdapter.updateData(it, id)
        }
        viewModel.tags.observe(this) {
            updateTagAdapter(it)
        }
        viewModel.users.observe(this) {
            viewModel.getTags("group", id.toString(), it.id)
        }
    }

    private fun initMediaAdapter() {
        mediaAdapter = IdolMediaListAdapter(mutableListOf())
        binding.detailsGroupMediaList.adapter = mediaAdapter
    }

    private fun initIdolAdapter() {
        idolAdapter = IdolGroupListAdapter(mutableListOf())
        binding.detailsGroupMemberList.adapter = idolAdapter
    }

    private fun initActivityAdapter() {
        activityAdapter = ActivityListAdapter(mutableListOf(), id)
        binding.detailsPartActivityList.adapter = activityAdapter
    }

    private fun updateView(data: IdolGroupList) {
        binding.apply {
            detailsTitle.text = data.name
            detailsLocationText.text = data.location
            detailsInfoText.text = data.groupDesc
        }
    }

    private fun updateTagAdapter(data: List<TagsList>) {
        if (data.isNotEmpty()) {
            showTagsList(data)
        } else {
            hideTagsListAndPromptUserToAddTag()
        }
    }

    private fun showTagsList(data: List<TagsList>) {
        binding.detailsEvaluateList.visibility = View.VISIBLE
        binding.detailsEvaluateList.adapter = createTagListAdapter(data)
        @SuppressLint("SetTextI18n")
        binding.detailsEvaluateInclude.text = "当前共${data.size}条，点我继续添加！"
        binding.detailsEvaluateInclude.setOnClickListener { checkUserLoginAndPrompt() }
    }

    private fun hideTagsListAndPromptUserToAddTag() {
        binding.detailsEvaluateList.visibility = View.GONE
        binding.detailsEvaluateInclude.text = "暂时还没有评价，点我添加一个"
        binding.detailsEvaluateInclude.setOnClickListener { checkUserLoginAndPrompt() }
    }

    private var lastClickTime: Long = 0
    private fun createTagListAdapter(data: List<TagsList>): TagListAdapter {
        return TagListAdapter(data, object : TagListAdapter.OnTagClickListener {
            override fun onTagClick(tag: TagsList) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime >= 2000) {
                    lastClickTime = currentTime
                    if (viewModel.users.value == null) {
                        promptUserToLogin()
                    } else {
                        viewModel.addTags("group", id, tag.content, "like_tag", tag.id) {
                            toastBack(it)
                        }
                    }
                } else {
                    toast("每2秒只能点一次，请不要频繁点击")
                }
            }
        })
    }

    private fun checkUserLoginAndPrompt() {
        if (viewModel.users.value == null) {
            promptUserToLogin()
        } else {
            showAddTagDialog()
        }
    }

    private fun showAddTagDialog() {
        val editText = EditText(applicationContext).apply {
            gravity = Gravity.CENTER
            hint = "请输入标签内容，最多十个字"
            filters = arrayOf<InputFilter>(InputFilter.LengthFilter(10))
        }
        var content = ""
        dialog(
            title = "",
            message = "",
            positiveButtonText = "确定",
            negativeButtonText = "取消",
            onPositive = {
                content = editText.text.toString()
                if (content.isEmpty()) toast("未填写内容") else {
                    viewModel.addTags("group", id, content, "create_tag", "") {
                        toastBack(it)
                    }
                }
            },
            onNegative = {},
            customView = editText,
            onDismiss = { if (content.isEmpty()) toast("未填写内容") }
        )
    }

    private fun promptUserToLogin() {
        dialog(
            "提示",
            "为了安全起见，添加/点赞 暂只支持已登录用户使用，请先登录",
            "去登录",
            "我就看看",
            { UiUtils.openPage(this@IdolDetailsActivity, LoginActivity::class.java) }
        )
    }

    private fun toastBack(type: String) {
        viewModel.getTags("group", id.toString(), viewModel.users.value?.id)
        when (type) {
            "error" -> toast("错误")
            "cancel_like" -> toast("取消点赞成功")
            "add_like" -> toast("点赞成功")
            "create_tag" -> toast("创建标签成功")
        }
    }
}