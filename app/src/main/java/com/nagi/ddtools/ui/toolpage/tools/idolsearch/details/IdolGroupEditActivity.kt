package com.nagi.ddtools.ui.toolpage.tools.idolsearch.details

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.children
import com.google.gson.Gson
import com.nagi.ddtools.data.MediaList
import com.nagi.ddtools.data.Resource
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import com.nagi.ddtools.databinding.ActivityIdolGroupEditBinding
import com.nagi.ddtools.resourceGet.NetGet
import com.nagi.ddtools.ui.base.DdToolsBindingBaseActivity
import com.nagi.ddtools.ui.widget.MediaSpinnerAndInput
import com.nagi.ddtools.utils.FileUtils
import com.nagi.ddtools.utils.UiUtils.openPage
import com.nagi.ddtools.utils.UiUtils.toast

class IdolGroupEditActivity : DdToolsBindingBaseActivity<ActivityIdolGroupEditBinding>() {
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private val viewModel: IdolGroupEditViewModel by viewModels()
    private var id = 0
    private lateinit var uri: Uri
    override fun createBinding(): ActivityIdolGroupEditBinding {
        return ActivityIdolGroupEditBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initViewModel()
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                handleActivityResult(result)
            }
    }

    private fun initView() {
        id = intent.extras?.getInt("id") ?: 0
        binding.titleInclude.apply {
            titleText.text = if (id == 0) "新增团体" else "编辑团体详情"
            titleBack.setOnClickListener { finish() }
        }
        binding.editMediaAdd.setOnClickListener { addMedia() }
        binding.editSubmit.setOnClickListener { if (checkEmpty()) editSubmit() }
        binding.editPreview.setOnClickListener { if (checkEmpty()) previewSubmit() }
    }

    private fun initViewModel() {
        viewModel.submitResult.observe(this) {
            toast(it.toString())
            finish()
        }
        if (id == 0) {
            binding.editImage.setOnClickListener {
                FileUtils.openImageGallery(this@IdolGroupEditActivity, resultLauncher)
            }
            binding.editSubmit.setOnClickListener {
                if (checkEmpty()) createSubmit()
            }
            return
        }
        viewModel.setId(id)
        viewModel.info.observe(this) {
            binding.apply {
                editName.setText(it.name)
                editLocation.setText(it.location)
                editDesc.setText(it.groupDesc)
                editImage.setImageUrl(it.imgUrl, false)
            }
        }
        viewModel.mediaList.observe(this) {
            for (media in it) {
                val mediaBody = MediaSpinnerAndInput(binding.root.context)
                mediaBody.setData(media)
                binding.editMediaLayout.addView(mediaBody)
            }
        }
    }

    private fun addMedia() {
        val mediaBody = MediaSpinnerAndInput(binding.root.context)
        binding.editMediaLayout.addView(mediaBody)
    }

    private fun getMediaList(name: String): String {
        if (binding.editMediaLayout.childCount == 0) Gson().toJson(mapOf("media" to ""))
        val mediaList = mutableListOf<MediaList>()
        binding.editMediaLayout.children.forEach { mediaView ->
            if (mediaView is MediaSpinnerAndInput) mediaView.getData(name)
                ?.let { mediaList.add(it) }
        }
        return Gson().toJson(mapOf("media" to mediaList))
    }

    private fun checkEmpty(): Boolean {
        if (binding.editName.text.isNullOrEmpty()) {
            binding.editNameLayout.error = "请填写团体名称"
            return false
        }
        if (binding.editLocation.text.isNullOrEmpty()) {
            binding.editLocationLayout.error = "请填写团体地点"
            return false
        }
        if (id == 0 && (!::uri.isInitialized || uri.toString().isEmpty())) {
            toast("请选择图片")
            return false
        }
        return true
    }


    private fun extractGroupData(versionIncrement: Int = 0): IdolGroupList? {
        val name = binding.editName.text.toString()
        val location = binding.editLocation.text.toString()
        val groupDesc = binding.editDesc.text.toString()
        val version = (viewModel.info.value?.version?.plus(versionIncrement)) ?: 1

        return if (name.isNotEmpty() && location.isNotEmpty() && groupDesc.isNotEmpty()) {
            IdolGroupList(
                id = viewModel.info.value?.id ?: System.currentTimeMillis().toString().takeLast(8)
                    .toInt(),
                imgUrl = viewModel.info.value?.imgUrl ?: "",
                name = name,
                version = version,
                location = location,
                groupDesc = groupDesc,
                ext = getMediaList(name),
                memberIds = viewModel.info.value?.memberIds,
                activityIds = viewModel.info.value?.activityIds
            )
        } else {
            null
        }
    }

    private fun editSubmit() {
        extractGroupData(versionIncrement = 1)?.let { submitGroupData ->
            viewModel.sendSubmitRequest(submitGroupData)
        } ?: toast("错误，有值为空！")
    }

    private fun createSubmit() {
        extractGroupData()?.let { data ->
            getImgUrl { imgUrl ->
                data.imgUrl = imgUrl.ifEmpty { "" }
                NetGet.editGroupInfo(data) { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            toast(resource.data)
                            finish()
                        }

                        is Resource.Error -> {
                            toast(resource.message)
                        }
                    }
                }
            }
        }
    }

    private fun previewSubmit() {
        extractGroupData()?.let { data ->
            val jsonPreview = Gson().toJson(data)
            openPage(
                this@IdolGroupEditActivity,
                IdolGroupDetailsActivity::class.java,
                false,
                Bundle().apply {
                    putInt("id",id)
                    putString("pageType", "preview")
                    putString("data", jsonPreview)
                })
        } ?: toast("错误，有值为空！")
    }


    private fun getImgUrl(callback: (String) -> Unit) {
        NetGet.uploadImage(
            applicationContext,
            uri,
            "groupImg/groupAvatar"
        ) { resource ->
            when (resource) {
                is Resource.Success -> {
                    callback(resource.data)
                }

                is Resource.Error -> {
                    toast(resource.message)
                    callback("")
                    finish()
                }
            }
        }
    }

    private fun handleActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uriEnd ->
                try {
                    uri = uriEnd
                    contentResolver.openInputStream(uriEnd)?.use { inputStream ->
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        binding.editImage.setImageBitmap(bitmap)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    toast("文件未找到或无法打开")
                } catch (e: Exception) {
                    e.printStackTrace()
                    toast("读取图片时出错")
                }
            } ?: toast("您未选择图片")
        }
    }
}
