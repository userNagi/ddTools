package com.nagi.ddtools.ui.toolpage.tools.idolsearch.details

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputFilter
import android.view.Gravity
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import com.google.gson.Gson
import com.nagi.ddtools.R
import com.nagi.ddtools.data.TagsList
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import com.nagi.ddtools.database.idolList.IdolList
import com.nagi.ddtools.database.idolList.IdolTag
import com.nagi.ddtools.databinding.ActivityIdolDetailsBinding
import com.nagi.ddtools.ui.adapter.IdolGroupListAdapter
import com.nagi.ddtools.ui.adapter.TagListAdapter
import com.nagi.ddtools.ui.base.DdToolsBindingBaseActivity
import com.nagi.ddtools.ui.minepage.user.login.LoginActivity
import com.nagi.ddtools.utils.UiUtils.dialog
import com.nagi.ddtools.utils.UiUtils.dpToPx
import com.nagi.ddtools.utils.UiUtils.openPage
import com.nagi.ddtools.utils.UiUtils.toast

class IdolDetailsActivity : DdToolsBindingBaseActivity<ActivityIdolDetailsBinding>() {
    private var id: Int? = 0
    private var lastClickTime: Long = 0
    private val viewModel: IdolDetailsViewModel by viewModels()
    override fun createBinding(): ActivityIdolDetailsBinding {
        return ActivityIdolDetailsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initView()
    }

    private fun initData() {
        id = intent.extras?.getInt("id")
        if (id == 0) initPreview() else {
            initViewModel()
            initTags()
        }
    }

    private fun initView() {
        binding.titleInclude.apply {
            titleBack.setOnClickListener { finish() }
            titleText.text = "偶像详情"
            titleRight.text = if (id == 0) "预览中" else "编辑"
            titleRight.setOnClickListener {
                if (id != 0) openPage(
                    this@IdolDetailsActivity, IdolEditActivity::class.java,
                    false, Bundle().apply { putInt("id", id!!) }
                )
            }
        }
    }

    private fun initPreview() {
        val data = Gson().fromJson(intent.extras?.getString("data"), IdolList::class.java)
        initViewModel(data)
        updateView(data)
    }

    private fun initTags() {
        updateTagAdapter(emptyList())
        viewModel.tags.observe(this) { updateTagAdapter(it) }

    }

    private fun initViewModel(data: IdolList? = null) {
        viewModel.setData(id ?: 0, data)
        viewModel.data.observe(this) { it?.let { it1 -> updateView(it1) } }
        viewModel.group.observe(this) { it?.let { it1 -> updateGroupAdapter(it1) } }
    }

    private fun updateView(data: IdolList) {
        setTag(data.tag)
        binding.detailsTitle.text = data.name
        binding.detailsInfoText.text = data.description
        binding.detailsGroupBirthdayText.text = data.birthday
        binding.detailsTitle.setCompoundDrawablesWithIntrinsicBounds(
            null, null, getSexIcon(data.sex), null
        )
    }

    private fun updateGroupAdapter(data: IdolGroupList) {
        binding.detailsGroup.adapter = IdolGroupListAdapter(mutableListOf(data))
    }

    private fun checkUserLoginAndPrompt() {
        if (viewModel.users.value == null) {
            promptUserToLogin()
        } else {
            showAddTagDialog()
        }
    }

    private fun updateTagAdapter(data: List<TagsList>) {
        if (data.isNotEmpty()) showTagsList(data) else hideTagsListAndPromptUserToAddTag()
    }

    private fun configurePrompt(text: String) {
        binding.detailsEvaluate.visibility = View.VISIBLE
        binding.detailsEvaluateInclude.text = text
        binding.detailsEvaluateInclude.visibility = View.VISIBLE
        binding.detailsEvaluateInclude.setOnClickListener { checkUserLoginAndPrompt() }
    }

    private fun showTagsList(data: List<TagsList>) {
        binding.detailsEvaluateList.visibility = View.VISIBLE
        binding.detailsEvaluateList.adapter = createTagListAdapter(data)
        configurePrompt("当前共${data.size}条，点我继续添加！")
    }

    private fun hideTagsListAndPromptUserToAddTag() {
        binding.detailsEvaluateList.visibility = View.GONE
        configurePrompt("暂时还没有评价，点我添加一个")
    }

    private fun createTagListAdapter(data: List<TagsList>) =
        TagListAdapter(data, object : TagListAdapter.OnTagClickListener {
            override fun onTagClick(tag: TagsList) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime >= 2000) {
                    lastClickTime = currentTime
                    if (viewModel.users.value == null) {
                        promptUserToLogin()
                    } else {
                        viewModel.addTags("group", id!!, tag.content, "like_tag", tag.id) {
                            toastBack(it)
                        }
                    }
                } else {
                    toast("每2秒只能点一次，请不要频繁点击")
                }
            }
        })

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
                    viewModel.addTags("group", id!!, content, "create_tag", "") {
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
            { openPage(this@IdolDetailsActivity, LoginActivity::class.java) }
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

    private fun getSexIcon(sex: String?) = when (sex) {
        "male" -> AppCompatResources.getDrawable(applicationContext, R.drawable.ic_male)
        "female" -> AppCompatResources.getDrawable(applicationContext, R.drawable.ic_female)
        else -> null
    }

    private fun setTag(tag: IdolTag?) {
        if (tag == null) return
        val background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = binding.root.context.dpToPx(4f)
        }.apply { setColor(Color.parseColor(tag.backColor)) }
        binding.detailsGroupTagText.text = tag.text
        binding.detailsGroupTagText.background = background
        binding.detailsGroupTagText.visibility = View.VISIBLE
        binding.detailsGroupTagText.setTextColor(Color.parseColor(tag.textColor))
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUser()
    }
}