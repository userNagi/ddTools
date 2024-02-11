package com.nagi.ddtools.ui.toolpage.tools.activitysearch.details

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.core.view.children
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.nagi.ddtools.data.MediaList
import com.nagi.ddtools.data.Resource
import com.nagi.ddtools.database.activityList.ActivityList
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import com.nagi.ddtools.databinding.ActivityEditActivityBinding
import com.nagi.ddtools.databinding.DialogAddGroupChooseBinding
import com.nagi.ddtools.databinding.DialogTimepickerBinding
import com.nagi.ddtools.resourceGet.NetGet
import com.nagi.ddtools.ui.adapter.IdolGroupListAdapter
import com.nagi.ddtools.ui.base.DdToolsBindingBaseActivity
import com.nagi.ddtools.ui.minepage.user.register.RegisterActivity
import com.nagi.ddtools.ui.widget.MediaSpinnerAndInput
import com.nagi.ddtools.utils.UiUtils
import com.nagi.ddtools.utils.UiUtils.dialog
import com.nagi.ddtools.utils.UiUtils.toast

class EditActivityActivity : DdToolsBindingBaseActivity<ActivityEditActivityBinding>() {
    private val viewModel: EditActivityViewModel by viewModels()
    private val adapter: IdolGroupListAdapter by lazy {
        IdolGroupListAdapter(mutableListOf()) { position, data ->
            setTimeTable(position, data)
        }
    }
    private var id = 0
    override fun createBinding(): ActivityEditActivityBinding {
        return ActivityEditActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = intent.extras?.getInt("id") ?: 0
        initView()
        initViewModel()
    }

    private fun initView() {
        if (id != 0) loadData()
        binding.activityPartList.adapter = this@EditActivityActivity.adapter
        binding.activityPartAdd.setOnClickListener { addIdol() }
        binding.activityVideosAdd.setOnClickListener { addVideo() }
        binding.titleInclude.apply {
            titleText.text = if (id == 0) "创建活动" else "修改活动"
            titleBack.setOnClickListener { finish() }
        }
        binding.activitySubmit.setOnClickListener {
            if (id == 0) createSubmit() else editSubmit()
        }
        binding.activityName.addTextChangedListener(RegisterActivity.RegisterWatching {
            binding.activityNameLayout.error = null
        })
        binding.activityDate.addTextChangedListener(RegisterActivity.RegisterWatching {
            binding.activityDateLayout.error = null
        })
        binding.activityLocation.addTextChangedListener(RegisterActivity.RegisterWatching {
            binding.activityLocationLayout.error = null
        })
        binding.activityLocationDesc.addTextChangedListener(RegisterActivity.RegisterWatching {
            binding.activityLocationDescLayout.error = null
        })
        binding.activityTime.addTextChangedListener(RegisterActivity.RegisterWatching {
            binding.activityTimeLayout.error = null
        })
        binding.activityPrice.addTextChangedListener(RegisterActivity.RegisterWatching {
            binding.activityPriceLayout.error = null
        })
        binding.activityBuyUrl.addTextChangedListener(RegisterActivity.RegisterWatching {
            binding.activityBuyUrlLayout.error = null
        })
        binding.activityWeibo.addTextChangedListener(RegisterActivity.RegisterWatching {
            binding.activityWeiboLayout.error = null
        })
    }

    private fun initViewModel() {
        viewModel.groupList.observe(this) {
            updateAdapter(it)
        }
    }

    private fun addIdol() {
        val dialogBinding = DialogAddGroupChooseBinding.inflate(layoutInflater)
        val dialogView = dialogBinding.root

        viewModel.location.observe(this) { locations ->
            dialogBinding.spinnerLocation.adapter = locations.toSpinnerAdapter()
        }
        dialogBinding.spinnerLocation.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val selectedLocation = parent.getItemAtPosition(position) as String
                    viewModel.groupData.value?.let { groups ->
                        val filteredGroups = groups.filter { it.location == selectedLocation }
                        dialogBinding.spinnerGroup.adapter =
                            filteredGroups.map { it.name }.toSpinnerAdapter()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    dialogBinding.spinnerGroup.adapter = null
                }
            }

        dialogBinding.buttonConfirm.setOnClickListener {
            val selectedGroupName = dialogBinding.spinnerGroup.selectedItem as String
            viewModel.groupData.value?.find { it.name == selectedGroupName }?.let { newData ->
                viewModel.addData(newData.copy(groupDesc = ""))
            }
        }
        dialogBinding.buttonCancel.setOnClickListener { UiUtils.hideDialog() }
        dialog("添加选项", "请选择", "", "", {}, {}, dialogView)
    }

    private fun addVideo() {
        binding.activityMediaList.addView(MediaSpinnerAndInput(binding.root.context))
    }

    private fun updateAdapter(data: List<IdolGroupList>) {
        adapter.swipeToDeleteEnabled = true
        adapter.isShowEditTime = true
        adapter.updateData(data)

        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        if (adapter.swipeToDeleteEnabled) {
                            adapter.removeAt(position)
                            viewModel.removeData(position)
                        } else {
                            adapter.notifyItemChanged(position)
                        }
                    }
                }
            }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.activityPartList)
    }

    private fun setTimeTable(position: Int, data: IdolGroupList) {
        val dialogBinding = DialogTimepickerBinding.inflate(layoutInflater)
        dialogBinding.timePicker1.setIs24HourView(true)
        dialogBinding.timePicker2.setIs24HourView(true)
        var startTime = String.format(
            "%02d:%02d",
            dialogBinding.timePicker1.hour,
            dialogBinding.timePicker1.minute
        )
        var endTime = String.format(
            "%02d:%02d",
            dialogBinding.timePicker1.hour,
            dialogBinding.timePicker1.minute
        )
        dialogBinding.timePicker1.setOnTimeChangedListener { _, selectedHour, selectedMinute ->
            startTime = String.format("%02d:%02d", selectedHour, selectedMinute)
        }
        dialogBinding.timePicker2.setOnTimeChangedListener { _, selectedHour, selectedMinute ->
            endTime = String.format("%02d:%02d", selectedHour, selectedMinute)
        }
        dialog(
            "设置时间表",
            "只需要输入开始和结束时间，持续时间会自动计算",
            "确定",
            "取消",
            {
                data.groupDesc = "$startTime-$endTime"
                adapter.editDesc(position, data)
                viewModel.editData(data)
            },
            {},
            dialogBinding.root
        )
    }

    private fun loadData() {

    }

    private fun checkEmpty(): Boolean {

        if (binding.activityName.text.isNullOrEmpty()) {
            binding.activityNameLayout.error = "活动名不能为空"
            return false
        }
        if (binding.activityDate.text.isNullOrEmpty()) {
            binding.activityDateLayout.error = "活动日期不能为空"
            return false
        }
        if (binding.activityLocation.text.isNullOrEmpty()) {
            binding.activityLocationLayout.error = "城市不能为空"
            return false
        }
        if (binding.activityLocationDesc.text.isNullOrEmpty()) {
            binding.activityLocationDescLayout.error = "活动地址不能为空"
            return false
        }
        if (binding.activityTime.text.isNullOrEmpty()) {
            binding.activityTimeLayout.error = "活动时间不能为空"
            return false
        }
        if (binding.activityPrice.text.isNullOrEmpty()) {
            binding.activityPriceLayout.error = "票价不能为空"
            return false
        }
        if (binding.activityBuyUrl.text.isNullOrEmpty()) {
            binding.activityBuyUrlLayout.error = "购买地址不能为空"
            return false
        }

        return true

    }

    private fun createSubmit() {
        if (checkEmpty()) {
            val activityData = ActivityList(
                id = System.currentTimeMillis().toString().takeLast(8).toInt(),
                name = binding.activityName.text.toString(),
                durationDate = binding.activityDate.text.toString(),
                durationTime = binding.activityTime.text.toString(),
                location = binding.activityLocation.text.toString(),
                locationDesc = binding.activityLocationDesc.text.toString(),
                price = binding.activityPrice.text.toString(),
                buyUrl = binding.activityBuyUrl.text.toString(),
                weiboUrl = binding.activityWeibo.text.toString(),
                infoDesc = binding.activityDesc.text.toString(),
                participatingGroup = getPartGroup(),
                biliUrl = getVideos(),
                ext = null
            )

            NetGet.insertActivity(activityData) { resource ->
                when (resource) {
                    is Resource.Success -> toast(resource.data)
                    is Resource.Error -> toast(resource.message)
                }
            }
        }
    }

    private fun editSubmit() {

    }

    private fun getPartGroup() = viewModel.groupList.value?.joinToString(separator = ",") {
        if (it.groupDesc.isNotEmpty()) "${it.id}-${it.groupDesc}" else "${it.id}"
    }


    private fun getVideos(): String {
        val mediaList: MutableList<MediaList> = mutableListOf()
        for (media in binding.activityMediaList.children) {
            if (media is MediaSpinnerAndInput) {
                media.getData(binding.activityName.text.toString())?.let { mediaList.add(it) }
            }
        }
        return if (mediaList.isEmpty()) "" else Gson().toJson(mediaList)
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