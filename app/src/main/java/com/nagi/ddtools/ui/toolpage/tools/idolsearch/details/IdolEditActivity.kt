package com.nagi.ddtools.ui.toolpage.tools.idolsearch.details

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.google.gson.Gson
import com.nagi.ddtools.data.MediaList
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import com.nagi.ddtools.database.idolList.IdolList
import com.nagi.ddtools.database.idolList.IdolTag
import com.nagi.ddtools.databinding.ActivityIdolEditBinding
import com.nagi.ddtools.databinding.DialogAddGroupChooseBinding
import com.nagi.ddtools.databinding.DialogSingleDateBinding
import com.nagi.ddtools.ui.base.DdToolsBindingBaseActivity
import com.nagi.ddtools.ui.widget.MediaSpinnerAndInput
import com.nagi.ddtools.ui.widget.dialog.IdolTagDialog
import com.nagi.ddtools.utils.DataUtils.getSex
import com.nagi.ddtools.utils.UiUtils.dialog
import com.nagi.ddtools.utils.UiUtils.dpToPx

class IdolEditActivity : DdToolsBindingBaseActivity<ActivityIdolEditBinding>() {
    private var id = 0
    private var groupId = 0
    private var tag: IdolTag? = null
    private lateinit var groupDialog: Dialog
    private lateinit var idolTagDialog: Dialog
    private val viewModel: IdolEditViewModel by viewModels()
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
            editSexText.adapter = setOf("女", "男", "其他").toSpinnerAdapter()
            editSexText.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
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
        binding.editSexText.tag = getSex(data.sex)
        binding.editBirthdayText.text = data.birthday
        binding.editGroup.isFocusable = false
        binding.editGroup.isCursorVisible = false
        binding.editGroup.isFocusableInTouchMode = false
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
        viewModel.groupLocation.observe(this) { locations ->
            dialogBinding.spinnerLocation.adapter = locations.toSpinnerAdapter()
        }
        dialogBinding.spinnerLocation.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long
                ) {
                    val selectedLocation = parent.getItemAtPosition(position) as String
                    val groupFilter = data.filter { it.location == selectedLocation }
                    dialogBinding.spinnerGroup.adapter =
                        groupFilter.map { it.name }.toSpinnerAdapter()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    dialogBinding.spinnerGroup.adapter = null
                }
            }

        groupDialog = Dialog(this).apply {
            setContentView(dialogView)
            dialogBinding.buttonConfirm.setOnClickListener {
                val selectedGroupName = dialogBinding.spinnerGroup.selectedItem as String
                val selectedLocation = dialogBinding.spinnerLocation.selectedItem as String
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

    private fun Collection<String>.toSpinnerAdapter(): ArrayAdapter<String> {
        return ArrayAdapter(
            applicationContext,
            android.R.layout.simple_spinner_item,
            this.toList()
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }
}
