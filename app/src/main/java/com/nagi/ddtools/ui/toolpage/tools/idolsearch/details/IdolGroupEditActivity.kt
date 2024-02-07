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
        if (binding.editDesc.text.isNullOrEmpty()) {
            binding.editDescLayout.error = "请填写团体描述"
            return true
        }
        if (!::uri.isInitialized && uri.toString().isEmpty()) {
            toast("请选择图片")
            return false
        }
        return true
    }


    private fun editSubmit() {
        val submitGroupData = viewModel.info.value?.copy(
            name = binding.editName.text.toString(),
            location = binding.editLocation.text.toString(),
            groupDesc = binding.editDesc.text.toString(),
            version = viewModel.info.value?.version?.plus(1) ?: 1,
            ext = getMediaList(binding.editName.text.toString())
        )
        if (submitGroupData != null) viewModel.sendSubmitRequest(submitGroupData)
        else toast("错误，有值为空！")
    }

    private fun createSubmit() {
        getImgUrl {
            val data = IdolGroupList(
                id = System.currentTimeMillis().toString().takeLast(8).toInt(),
                imgUrl = it.ifEmpty { "" },
                name = binding.editName.text.toString(),
                version = 1,
                location = binding.editLocation.text.toString(),
                groupDesc = binding.editDesc.text.toString(),
                ext = getMediaList(binding.editName.text.toString()),
                memberIds = null,
                activityIds = null
            )
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
            result.data?.data?.let { uri ->
                try {
                    contentResolver.openInputStream(uri)?.use { inputStream ->
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
