package com.nagi.ddtools.ui.toolpage.tools.activitysearch.details

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import android.view.Gravity
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nagi.ddtools.R
import com.nagi.ddtools.data.MediaList
import com.nagi.ddtools.data.PageState
import com.nagi.ddtools.data.TagsList
import com.nagi.ddtools.database.activityList.ActivityList
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import com.nagi.ddtools.database.localCollect.LocalCollect
import com.nagi.ddtools.databinding.ActivityDetailsActivityBinding
import com.nagi.ddtools.databinding.DialogSingleInputBinding
import com.nagi.ddtools.databinding.ListIdolMediaBinding
import com.nagi.ddtools.ui.adapter.IdolGroupListAdapter
import com.nagi.ddtools.ui.adapter.TagListAdapter
import com.nagi.ddtools.ui.base.DdToolsBindingBaseActivity
import com.nagi.ddtools.ui.minepage.user.login.LoginActivity
import com.nagi.ddtools.utils.FileUtils.getDrawableForMedia
import com.nagi.ddtools.utils.MapUtils
import com.nagi.ddtools.utils.UiUtils.dialog
import com.nagi.ddtools.utils.UiUtils.openPage
import com.nagi.ddtools.utils.UiUtils.openUrl
import com.nagi.ddtools.utils.UiUtils.toast

class ActivityDetailsActivity : DdToolsBindingBaseActivity<ActivityDetailsActivityBinding>() {
    private val viewModel: ActivityDetailsViewModel by viewModels()
    private var pageState = PageState.VIEW
    private lateinit var groupAdapter: IdolGroupListAdapter
    private var isAdapterInitialized = false
    private var id = 0
    override fun createBinding(): ActivityDetailsActivityBinding {
        return ActivityDetailsActivityBinding.inflate(layoutInflater)
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
        binding.apply {
            titleInclude.apply {
                titleBack.setOnClickListener { finish() }
                titleText.text = "活动详情"
                titleRight.text = "编辑"
                titleRight.visibility = View.VISIBLE
                titleRight.setOnClickListener {
                    openPage(
                        this@ActivityDetailsActivity,
                        EditActivityActivity::class.java,
                        false,
                        Bundle().apply { putInt("id", id) }
                    )
                }
            }
            detailsEvaluate.text = "评价"
            val layoutManager = FlexboxLayoutManager(applicationContext).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.FLEX_START
            }
            detailsEvaluateList.layoutManager = layoutManager
            detailsCollect.setOnClickListener { viewModel.setCollect(createCollectData()) }
        }
        initGroupAdapter()
        if (pageState == PageState.VIEW) initViewModel()
        else initFromData()
    }

    private fun initViewModel() {
        viewModel.getUser()
        viewModel.setId(id)
        viewModel.getTags("activity", id.toString())
        updateTagAdapter(emptyList())
        viewModel.data.observe(this) {
            updateView(it)
        }
        viewModel.isCollection.observe(this) {
            updateCollection(it)
        }
        viewModel.tags.observe(this) { data ->
            updateTagAdapter(data)
        }
        viewModel.groupList.observe(this) { data ->
            upGroupAdapter(data)
        }
    }

    private fun initFromData() {
        val dataJson = intent.extras?.getString("data")
        val data = Gson().fromJson(dataJson, ActivityList::class.java)
        binding.apply {
            detailsTitle.text = data.name
            detailsDateText.text = data.durationDate
            detailsLocationText.text = data.location
            detailsTimeText.text = data.durationTime
            detailsPriceText.text = data.price
            detailsCollect.visibility = View.GONE
            detailsEvaluate.visibility = View.GONE
            detailsEvaluateList.visibility = View.GONE
            detailsEvaluateInclude.visibility = View.GONE
            if (!data.biliUrl.isNullOrEmpty()) {
                detailsVideoLink.visibility = View.VISIBLE
                addMediaLayout(data.biliUrl)
            }
            if (data.infoDesc.isNotEmpty()) detailsDescText.text = data.infoDesc
        }
        viewModel.setGroupList(data.participatingGroup)
        viewModel.groupList.observe(this@ActivityDetailsActivity) { groupAdapter.updateData(it) }
    }

    private fun initGroupAdapter() {
        if (!isAdapterInitialized) {
            groupAdapter = IdolGroupListAdapter(mutableListOf())
        }
        groupAdapter.isShowTimeTable = true
        binding.detailsGroupList.adapter = groupAdapter
        isAdapterInitialized = true
    }

    private fun updateView(data: ActivityList) {
        binding.apply {
            detailsTitle.text = data.name
            detailsVideoLink.visibility = View.GONE
            detailsDateText.text = data.durationDate
            detailsTimeText.text = data.durationTime
            detailsPriceText.text = data.price
            detailsLocationText.apply {
                text = data.locationDesc
                setOnClickListener { MapUtils.chooseLocation(context, data.locationDesc) }
            }
            if (!data.biliUrl.isNullOrEmpty()) {
                detailsVideoLink.visibility = View.VISIBLE
                addMediaLayout(data.biliUrl)
            }
            if (data.infoDesc.isNotEmpty()) detailsDescText.text = data.infoDesc
        }
    }

    private fun addMediaLayout(biliUrl: String) {
        val mediaResult: MutableList<MediaList>
        val itemType = object : TypeToken<List<MediaList>>() {}.type
        mediaResult = Gson().fromJson(biliUrl, itemType)
        for (media in mediaResult) {
            val mediaBody = ListIdolMediaBinding.inflate(layoutInflater)
            mediaBody.mediaName.text = media.name
            mediaBody.root.setOnClickListener { openUrl(media.url) }
            mediaBody.mediaIcon.setImageDrawable(
                getDrawableForMedia(mediaBody.root.context, media.type)
            )
            binding.detailsVideoLinkLayout.addView(mediaBody.root)
        }
    }

    private fun updateCollection(data: Boolean) {
        binding.detailsCollect.visibility = View.VISIBLE
        binding.detailsCollect.setImageDrawable(
            AppCompatResources.getDrawable(
                applicationContext,
                if (data) R.drawable.baseline_collect else R.drawable.baseline_un_collect
            )
        )
    }

    private fun updateTagAdapter(data: List<TagsList>) {
        if (data.isNotEmpty()) showTagsList(data)
        else hideTagsListAndPromptUserToAddTag()

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
    private fun createTagListAdapter(data: List<TagsList>) =
        TagListAdapter(data, object : TagListAdapter.OnTagClickListener {
            override fun onTagClick(tag: TagsList) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime >= 2000) {
                    lastClickTime = currentTime
                    viewModel.addTags("activity", id, tag.content, "like_tag", tag.id) {
                        toastBack(
                            it
                        )
                    }
                } else toast("每2秒只能点一次，请不要频繁点击")
            }
        })

    private fun checkUserLoginAndPrompt() {
        if (viewModel.users.value == null) promptUserToLogin() else showAddTagDialog()
    }

    private fun createCollectData() = LocalCollect(
        collectId = id,
        collectType = "activity",
        userId = viewModel.users.value?.id ?: 0
    )

    private fun showAddTagDialog() {
        val editBinding = DialogSingleInputBinding.inflate(layoutInflater)
        editBinding.inputText.apply {
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
                content = editBinding.inputText.text.toString()
                if (content.isEmpty()) {
                    toast("未填写内容")
                } else {
                    viewModel.addTags("activity", id, content, "create_tag", "") {
                        toastBack(it)
                    }
                }
            },
            onNegative = {},
            customView = editBinding.root,
            onDismiss = {
                if (content.isEmpty()) {
                    toast("未填写内容")
                }
            }
        )
    }

    private fun promptUserToLogin() {
        dialog(
            "提示",
            "为了安全起见，添加/点赞 暂只支持已登录用户使用，请先登录",
            "去登录",
            "我就看看",
            { openPage(this@ActivityDetailsActivity, LoginActivity::class.java) }
        )
    }

    private fun toastBack(type: String) {
        viewModel.getTags("activity", id.toString(), viewModel.users.value?.id)
        when (type) {
            "error" -> toast("错误")
            "cancel_like" -> toast("取消点赞成功")
            "add_like" -> toast("点赞成功")
            "create_tag" -> toast("创建标签成功")
        }
    }

    private fun upGroupAdapter(data: List<IdolGroupList>) {
        groupAdapter.updateData(data)
    }
}