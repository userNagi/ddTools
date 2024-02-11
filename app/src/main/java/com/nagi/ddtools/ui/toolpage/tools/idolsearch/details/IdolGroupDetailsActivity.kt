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
import com.google.gson.Gson
import com.nagi.ddtools.data.PageState
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

class IdolGroupDetailsActivity : DdToolsBindingBaseActivity<ActivityIdolDetailsBinding>() {
    private val viewModel: IdolGroupDetailsViewModel by viewModels()
    private var id: Int = 0
    private var pageState = PageState.VIEW
    private lateinit var mediaAdapter: IdolMediaListAdapter
    private lateinit var idolAdapter: IdolGroupListAdapter
    private lateinit var activityAdapter: ActivityListAdapter
    override fun createBinding(): ActivityIdolDetailsBinding {
        return ActivityIdolDetailsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUser()
    }

    private fun initView() {
        id = intent.extras?.getInt("id") ?: 0
        pageState = if (intent.extras?.getString("pageType").isNullOrEmpty())
            PageState.VIEW else PageState.PREVIEW
        initTitle()
        initListView()
        if (pageState == PageState.VIEW) initViewModel()
        else {
            initFromData()
        }
    }

    private fun initTitle() {
        binding.titleInclude.titleBack.setOnClickListener { finish() }
        binding.titleInclude.titleText.text = "组合详情"
        binding.titleInclude.titleRight.visibility = View.VISIBLE
        if (pageState == PageState.VIEW) {
            binding.titleInclude.titleRight.text = "编辑"
            binding.titleInclude.titleRight.setOnClickListener {
                UiUtils.openPage(
                    this@IdolGroupDetailsActivity,
                    IdolGroupEditActivity::class.java,
                    false,
                    Bundle().apply { putInt("id", id) })
            }
        } else binding.titleInclude.titleRight.text = "预览中"
    }

    private fun initListView() {
        val layoutManager = FlexboxLayoutManager(applicationContext).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }
        binding.detailsEvaluateList.layoutManager = layoutManager
        mediaAdapter = IdolMediaListAdapter(mutableListOf())
        binding.detailsGroupMediaList.adapter = mediaAdapter
        idolAdapter = IdolGroupListAdapter(mutableListOf())
        binding.detailsGroupMemberList.adapter = idolAdapter
        activityAdapter = ActivityListAdapter(mutableListOf(), id)
        binding.detailsPartActivityList.adapter = activityAdapter
    }

    private fun initViewModel() {
        viewModel.apply {
            setId(id)
            getTags("group", id.toString())
            updateTagAdapter(emptyList())
            data.observe(this@IdolGroupDetailsActivity) { updateView(it) }
            tags.observe(this@IdolGroupDetailsActivity) { updateTagAdapter(it) }
            medias.observe(this@IdolGroupDetailsActivity) { mediaAdapter.updateDate(it) }
            memberList.observe(this@IdolGroupDetailsActivity) { idolAdapter.updateData(it) }
            activityList.observe(this@IdolGroupDetailsActivity) {
                activityAdapter.updateData(it, id)
            }
            users.observe(this@IdolGroupDetailsActivity) {
                viewModel.getTags("group", id.toString(), it.id)
            }
        }
    }

    private fun initFromData() {
        val dataJson = intent.extras?.getString("data")
        val data = Gson().fromJson(dataJson, IdolGroupList::class.java)
        binding.apply {
            detailsTitle.text = data.name
            detailsLocationText.text = data.location
            detailsInfoText.text = data.groupDesc
            detailsEvaluateInclude.visibility = View.GONE
            detailsEvaluateList.visibility = View.GONE
        }
        viewModel.apply {
            data.activityIds?.let { if (it.isNotEmpty()) loadActivityData(0, it) }
            data.memberIds?.let { if (it.isNotEmpty()) loadMemberData(0, it) }
            data.ext.let { if (it.isNotEmpty()) loadMediaData(0, it) }
            activityList.observe(this@IdolGroupDetailsActivity) { activityAdapter.updateData(it,id) }
            memberList.observe(this@IdolGroupDetailsActivity) { idolAdapter.updateData(it) }
            medias.observe(this@IdolGroupDetailsActivity) { mediaAdapter.updateDate(it) }
        }
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
            { UiUtils.openPage(this@IdolGroupDetailsActivity, LoginActivity::class.java) }
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