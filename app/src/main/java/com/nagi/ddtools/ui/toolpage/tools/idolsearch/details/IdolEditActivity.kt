package com.nagi.ddtools.ui.toolpage.tools.idolsearch.details

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.children
import com.google.gson.Gson
import com.nagi.ddtools.data.MediaList
import com.nagi.ddtools.data.Resource
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import com.nagi.ddtools.database.idolList.IdolList
import com.nagi.ddtools.database.idolList.IdolTag
import com.nagi.ddtools.databinding.ActivityIdolEditBinding
import com.nagi.ddtools.databinding.DialogAddGroupChooseBinding
import com.nagi.ddtools.resourceGet.NetGet
import com.nagi.ddtools.ui.base.DdToolsBindingBaseActivity
import com.nagi.ddtools.ui.minepage.user.register.RegisterActivity
import com.nagi.ddtools.ui.widget.MediaSpinnerAndInput
import com.nagi.ddtools.ui.widget.dialog.IdolTagDialog
import com.nagi.ddtools.utils.DataUtils.getImgUrl
import com.nagi.ddtools.utils.DataUtils.getSex
import com.nagi.ddtools.utils.DataUtils.toSpinnerAdapter
import com.nagi.ddtools.utils.FileUtils
import com.nagi.ddtools.utils.UiUtils.dpToPx
import com.nagi.ddtools.utils.UiUtils.openPage
import com.nagi.ddtools.utils.UiUtils.showLoading
import com.nagi.ddtools.utils.UiUtils.toast

class IdolEditActivity : DdToolsBindingBaseActivity<ActivityIdolEditBinding>() {
    private var id = 0
    private var groupId = 0
    private lateinit var uri: Uri
    private var tag: IdolTag? = null
    private var sexList = listOf("女", "男", "其他")
    private lateinit var groupDialog: Dialog
    private lateinit var idolTagDialog: Dialog
    private val viewModel: IdolEditViewModel by viewModels()
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    override fun createBinding(): ActivityIdolEditBinding {
        return ActivityIdolEditBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
        initViewModel()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        binding.apply {
            titleInclude.apply {
                titleText.text = "编辑偶像详情"
                titleBack.setOnClickListener { finish() }
            }
            editGroup.setOnClickListener { groupDialog.show() }
            editTagText.setOnClickListener { idolTagDialog.show() }
            editSexText.adapter = sexList.toSpinnerAdapter(applicationContext)
            editImage.setOnClickListener {
                FileUtils.openImageGallery(this@IdolEditActivity, resultLauncher)
            }
            editName.addTextChangedListener(RegisterActivity.RegisterWatching {
                binding.editNameLayout.error = null
            })
            editLocation.addTextChangedListener(RegisterActivity.RegisterWatching {
                binding.editLocationLayout.error = null
            })
            editSexText.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p: AdapterView<*>?, v: View?, po: Int, id: Long) {}
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            editSubmit.setOnClickListener { if (checkEmpty()) createSubmit() }
            editPreview.setOnClickListener { if (checkEmpty()) createPreview() }
            editBirthdayText.setOnClickListener {
                val year = 2000
                val month = 1
                val day = 1

                val datePickerDialog = DatePickerDialog(
                    this@IdolEditActivity,
                    { _, y, m, d -> editBirthdayText.text = "$y-$m-$d" },
                    year,
                    month,
                    day
                )
                datePickerDialog.show()

            }
            editMediaAdd.setOnClickListener {
                editMediaLayout.addView(MediaSpinnerAndInput(root.context))
            }
        }
    }

    private fun initData() {
        id = intent.extras?.getInt("id") ?: 0
        intent.extras?.getString("data")?.let { dataString ->
            runCatching { Gson().fromJson(dataString, IdolList::class.java) }
                .onSuccess(::updateData)
        }
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                handleActivityResult(result)
            }

    }

    private fun initViewModel() {
        viewModel.groupInfo.observe(this) { data -> createGroupDialogView(data) }
        viewModel.tagData.observe(this) { data -> createIdolTagDialogView(data) }
        viewModel.mediaList.observe(this) { data -> setMedia(data) }
    }

    private fun updateData(data: IdolList) {
        setTag(data.tag)
        binding.editName.setText(data.name)
        binding.editGroup.setText(data.groupName)
        binding.editDesc.setText(data.description)
        binding.editLocation.setText(data.location)
        binding.editBirthdayText.text = data.birthday
        binding.editGroup.isFocusable = false
        binding.editGroup.isCursorVisible = false
        binding.editGroup.isFocusableInTouchMode = false
        binding.editSexText.setSelection(sexList.indexOf(getSex(data.sex)))
        if (data.imageUrl.isNotEmpty()) binding.editImage.setImageUrl(data.imageUrl)
    }

    private fun setTag(tag: IdolTag?) {
        tag?.textColor?.let {
            val background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = binding.root.context.dpToPx(4f)
                color = ColorStateList.valueOf(Color.parseColor(tag.backColor))
            }
            binding.editTagText.text = tag.text
            binding.editTagText.background = background
            binding.editTagText.setTextColor(Color.parseColor(it))
        }
    }

    private fun setMedia(mediaList: List<MediaList>) {
        val mediaBody = MediaSpinnerAndInput(binding.root.context)
        for (media in mediaList) {
            mediaBody.setData(media)
            binding.editMediaLayout.addView(mediaBody)
        }
    }

    private fun createGroupDialogView(data: List<IdolGroupList>) {
        val dialogBinding = DialogAddGroupChooseBinding.inflate(layoutInflater)
        val dialogView = dialogBinding.root
        val spinner = dialogBinding.spinnerLocation
        viewModel.groupLocation.observe(this) { spinner.adapter = it.toSpinnerAdapter(this) }
        spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long
                ) {
                    val selectedLocation = parent.getItemAtPosition(position) as String
                    val groupFilter = data.filter { it.location == selectedLocation }
                    dialogBinding.spinnerGroup.adapter =
                        groupFilter.map { it.name }.toSpinnerAdapter(applicationContext)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    dialogBinding.spinnerGroup.adapter = null
                }
            }

        groupDialog = Dialog(this).apply {
            setContentView(dialogView)
            dialogBinding.buttonConfirm.setOnClickListener {
                val selectedGroupName = dialogBinding.spinnerGroup.selectedItem as String
                val selectedLocation = spinner.selectedItem as String
                groupId = viewModel.groupInfo.value?.find { it.name == selectedGroupName }?.id ?: 0
                binding.editGroup.setText(selectedGroupName)
                binding.editLocation.setText(selectedLocation)
                this.dismiss()
            }
            dialogBinding.buttonCancel.setOnClickListener { this.dismiss() }
        }
    }

    private fun createIdolTagDialogView(data: List<IdolTag>) {
        idolTagDialog = IdolTagDialog(this, data).apply {
            callback = object : IdolTagDialog.Callback {
                override fun onConfirm(selectedTag: IdolTag) {
                    tag = selectedTag
                    viewModel.setTag(selectedTag)
                    setTag(tag)
                }

                override fun onCancel() {}
            }
        }
    }

    private fun checkEmpty(): Boolean {
        if (binding.editName.text.isNullOrEmpty()) {
            binding.editNameLayout.error = "名称不能为空"
            return false
        }
        if (binding.editLocation.text.isNullOrEmpty()) {
            binding.editLocationLayout.error = "地区不能为空"
            return false
        }
        if (id == 0 && (!::uri.isInitialized || uri.toString().isEmpty())) {
            toast("请选择图片")
            return false
        }
        return true
    }

    private fun createResult(): IdolList? {
        val data = viewModel.info.value
        val name = binding.editName.text.toString()
        val location = binding.editLocation.text.toString()
        val sex = getSex(binding.editSexText.selectedItem.toString())
        val birthday =
            binding.editBirthdayText.text.toString().let { if (it == "-    -    -") "" else it }
        val groupName = binding.editGroup.text.toString()
        val desc = binding.editDesc.text.toString()

        return if (name.isNotEmpty() && location.isNotEmpty())
            IdolList(
                id = data?.id ?: System.currentTimeMillis().toString().takeLast(8)
                    .toInt(),
                name = name,
                description = desc,
                imageUrl = data?.imageUrl ?: "",
                tag = tag,
                birthday = birthday,
                sex = sex,
                location = location,
                groupName = groupName,
                groupId = groupId,
                ext = getMediaList(name),
                version = "1"
            ) else null
    }

    private var cachedImgUrl: String? = null
    private fun getImageUrl(): String? {
        if (cachedImgUrl.isNullOrEmpty()) {
            getImgUrl(applicationContext, uri, "idolImage/idolAvatar") { imgUrl ->
                cachedImgUrl = imgUrl
            }
        }
        return cachedImgUrl
    }


    private fun createSubmit() {
        showLoading(applicationContext)
        createResult()?.let { data ->
            data.imageUrl = getImageUrl() ?: "".ifEmpty { "" }
            NetGet.editIdol(data) { resource ->
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

    private fun createPreview() {
        createResult()?.let { data ->
            data.imageUrl = ""
            openPage(
                this@IdolEditActivity,
                IdolDetailsActivity::class.java,
                false,
                Bundle().apply {
                    putInt("id", id)
                    putString("pageType", "preview")
                    putString("data", Gson().toJson(data))
                })

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

    private fun getMediaList(name: String): String {
        if (binding.editMediaLayout.childCount == 0) Gson().toJson(mapOf("media" to ""))
        val mediaList = mutableListOf<MediaList>()
        binding.editMediaLayout.children.forEach { mediaView ->
            if (mediaView is MediaSpinnerAndInput) mediaView.getData(name)
                ?.let { mediaList.add(it) }
        }
        return Gson().toJson(mapOf("media" to mediaList))
    }
}
