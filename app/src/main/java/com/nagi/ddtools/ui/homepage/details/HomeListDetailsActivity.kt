package com.nagi.ddtools.ui.homepage.details

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.nagi.ddtools.database.homePagList.HomePageList
import com.nagi.ddtools.databinding.ActivityHomeListDetailsBinding
import com.nagi.ddtools.databinding.DialogSingleInputBinding
import com.nagi.ddtools.ui.adapter.HomePageListAdapter
import com.nagi.ddtools.ui.base.DdToolsBindingBaseActivity
import com.nagi.ddtools.ui.homepage.HomeViewModel
import com.nagi.ddtools.utils.FileUtils
import com.nagi.ddtools.utils.FileUtils.openImageGallery
import com.nagi.ddtools.utils.UiUtils.dialog
import com.nagi.ddtools.utils.UiUtils.toast

class HomeListDetailsActivity : DdToolsBindingBaseActivity<ActivityHomeListDetailsBinding>() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var id = 0
    private lateinit var currentData: HomePageList
    override fun createBinding(): ActivityHomeListDetailsBinding {
        return ActivityHomeListDetailsBinding.inflate(layoutInflater)
    }

    private val adapter: HomePageListAdapter by lazy {
        HomePageListAdapter(mutableListOf(), lifecycleScope, false) { position, data ->
            showDeleteDialog(position, data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                handleActivityResult(result)
            }
        id = intent.extras?.getInt("id") ?: 0
        initView()
        initAdapter()
        viewModel.loadHomePageList()
        viewModel.homeListData.observe(this) {
            updateView(it)
        }
    }

    private fun initView() {
        binding.apply {
            titleInclude.apply {
                titleBack.setOnClickListener { finish() }
                titleText.text = "详细信息"
                titleRight.apply {
                    text = "添加"
                    visibility = View.VISIBLE
                    setOnClickListener {
                        openImageGallery(this@HomeListDetailsActivity, resultLauncher)
                    }
                }
            }
            detailsInfoText.setOnClickListener {
                updateData(currentData)
            }
        }
    }

    private fun updateView(homePageList: List<HomePageList>) {
        binding.apply {
            currentData = homePageList.filter { it.id == id }[0]
            val data = homePageList.filter { it.parent == id }
            detailsTitle.text = currentData.name
            detailsInfoText.text = currentData.info
            detailsInclude.text = if (data.isNotEmpty()) "当前共${data.size}条" else ""
            adapter.updateData(data)
        }
    }

    private fun initAdapter() {
        binding.detailsIncludeDetails.apply {
            adapter = this@HomeListDetailsActivity.adapter
            layoutManager = GridLayoutManager(context, 2)
        }
    }

    private fun handleActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val editBinding = DialogSingleInputBinding.inflate(layoutInflater)
                editBinding.inputText.apply {
                    hint = "请输入保存名"
                }
                val endUrl = FileUtils.moveImage(this, uri, "homePageList").toString()
                var name = ""

                dialog(
                    title = "",
                    message = "",
                    positiveButtonText = "确定",
                    negativeButtonText = "取消",
                    onPositive = {
                        name = editBinding.inputText.text.toString()
                        if (name.isNotEmpty()) {
                            val data = HomePageList(
                                System.currentTimeMillis().toString().substring(5).toInt(),
                                endUrl,
                                name,
                                "",
                                id
                            )
                            viewModel.addData(data)
                        } else {
                            toast("输入为空")
                            FileUtils.deleteWithPath(endUrl)
                        }
                    },
                    onNegative = {
                    },
                    customView = editBinding.root,
                    onDismiss = { if (name.isEmpty()) FileUtils.deleteWithPath(endUrl) }
                )
            }
        } else {
            toast("您未选择图片")
        }
    }

    private fun showDeleteDialog(position: Int, data: HomePageList) {
        dialog(
            title = "删除此项",
            message = "你确定要删除这一项嘛？",
            positiveButtonText = "确定",
            onPositive = {
                adapter.removeAt(position)
                viewModel.removeData(data)
                FileUtils.deleteWithPath(data.imgUrl)
            },
            negativeButtonText = "取消",
            onNegative = {}
        )
    }

    private fun updateData(data: HomePageList) {
        val editBinding = DialogSingleInputBinding.inflate(layoutInflater)
        editBinding.inputText.apply {
            hint = "请输入简介"
        }
        dialog(
            title = "输入简介",
            message = "",
            positiveButtonText = "确定",
            negativeButtonText = "取消",
            onPositive = {
                binding.detailsInfoText.text = editBinding.inputText.text
                val dataForUpdate = data.copy(info = binding.detailsInfoText.text.toString())
                viewModel.updateData(dataForUpdate)
            },
            onNegative = {
            },
            customView = editBinding.root,
            onDismiss = {
            }
        )
    }

}