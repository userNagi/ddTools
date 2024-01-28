package com.nagi.ddtools.ui.toolpage.tools.groupwho

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nagi.ddtools.database.idolGroupList.IdolGroupList
import com.nagi.ddtools.databinding.ActivityGroupWhoAcivityBinding
import com.nagi.ddtools.databinding.DialogAddGroupChooseBinding
import com.nagi.ddtools.ui.adapter.IdolGroupListAdapter
import com.nagi.ddtools.ui.base.DdToolsBindingBaseActivity
import com.nagi.ddtools.utils.UiUtils
import com.nagi.ddtools.utils.UiUtils.dialog

class ChooseWhoActivity : DdToolsBindingBaseActivity<ActivityGroupWhoAcivityBinding>() {

    private val viewModel: ChooseWhoViewModel by viewModels()
    private lateinit var adapter: IdolGroupListAdapter
    private var isAdapterInitialized = false
    override fun createBinding(): ActivityGroupWhoAcivityBinding {
        return ActivityGroupWhoAcivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        intent.extras?.getString("name")?.let { viewModel.initData(it) }
        viewModel.groupList.observe(this) { data ->
            updateAdapter(data)
            initSelector(data.size)
            if (data.isNotEmpty()) updateView() else binding.chooseWhoClick.visibility = View.GONE
        }
    }

    private fun initView() {
        binding.apply {
            titleInclude.titleText.text = "今天切谁"
            titleInclude.titleBack.setOnClickListener {
                finish()
            }
            pageTitle.text = "已选列表"
            chooseWhoAdd.text = "点我添加选项"
            chooseWhoClick.text = "开始随机"
            pageSubtitle.text = "左滑可删除"
            chooseWhoAdd.setOnClickListener {
                addData()
            }
            chooseWhoClick.visibility = View.GONE
        }
        initAdapter()
    }

    private fun initAdapter() {
        if (!isAdapterInitialized) {
            adapter = IdolGroupListAdapter(mutableListOf())
        }
        binding.chooseWhoList.adapter = adapter
        isAdapterInitialized = true
    }

    private fun initSelector(num: Int) {
        binding.chooseWhoNum.apply {
            adapter = ArrayAdapter(
                this@ChooseWhoActivity,
                android.R.layout.simple_spinner_item,
                (1..num).map { it })
        }
    }

    private fun updateView() {
        binding.apply {
            chooseWhoClick.visibility = View.VISIBLE
            chooseWhoClick.setOnClickListener {
                val x = chooseWhoNum.selectedItem.toString().toInt()
                val randomIndices = (viewModel.groupList.value!!.indices).shuffled().take(x)
                val selectedItems =
                    randomIndices.map { viewModel.groupList.value!![it] }.toMutableList()
                val context = this@ChooseWhoActivity
                val listAdapter = RecyclerView(context)
                listAdapter.layoutManager = LinearLayoutManager(context)
                listAdapter.adapter = IdolGroupListAdapter(selectedItems)
                dialog(
                    title = "结果",
                    message = "恭喜你抽中了：\n快去切吧！",
                    positiveButtonText = "确定",
                    negativeButtonText = "取消",
                    onPositive = {},
                    onNegative = {},
                    customView = listAdapter
                )
            }
        }
    }

    private fun addData() {
        val dialogBinding = DialogAddGroupChooseBinding.inflate(layoutInflater)
        val dialogView = dialogBinding.root

        viewModel.location.observe(this) { locations ->
            dialogBinding.spinnerLocation.adapter = locations.toSpinnerAdapter()
        }

        viewModel.getGroupData()

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
                viewModel.addData(newData)
            }
        }
        dialogBinding.buttonCancel.setOnClickListener { UiUtils.hideDialog() }
        dialog("添加选项", "请选择", "", "", {}, {}, dialogView
        )
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


    private fun updateAdapter(data: List<IdolGroupList>) {
        adapter.updateData(data)

        adapter.swipeToDeleteEnabled = true

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
        itemTouchHelper.attachToRecyclerView(binding.chooseWhoList)
    }
}