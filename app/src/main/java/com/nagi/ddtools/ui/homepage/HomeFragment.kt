package com.nagi.ddtools.ui.homepage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nagi.ddtools.data.Resource
import com.nagi.ddtools.data.UpdateInfo
import com.nagi.ddtools.database.homePagList.HomePageList
import com.nagi.ddtools.databinding.DialogLongClickBinding
import com.nagi.ddtools.databinding.DialogSingleInputBinding
import com.nagi.ddtools.databinding.FragmentHomeBinding
import com.nagi.ddtools.resourceGet.NetGet
import com.nagi.ddtools.ui.adapter.HomePageListAdapter
import com.nagi.ddtools.utils.FileUtils
import com.nagi.ddtools.utils.FileUtils.moveImage
import com.nagi.ddtools.utils.FileUtils.openImageGallery
import com.nagi.ddtools.utils.LogUtils
import com.nagi.ddtools.utils.NetUtils
import com.nagi.ddtools.utils.PrefsUtils
import com.nagi.ddtools.utils.UiUtils.dialog
import com.nagi.ddtools.utils.UiUtils.toast
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding error")
    private val adapter: HomePageListAdapter by lazy {
        HomePageListAdapter(mutableListOf(), lifecycleScope, true) { position, data ->
            showEditDialog(position, data)
        }
    }
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var addLauncher: ActivityResultLauncher<Intent>
    private lateinit var editLauncher: ActivityResultLauncher<Intent>
    private var currentSelectedData: HomePageList? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        bundle: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        addLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                addResult(result)
            }
        editLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                editResult(result)
            }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initNetData()
        initAdapter()
        viewModel.loadHomePageList()
        viewModel.homeListData.observe(viewLifecycleOwner) { data ->
            updateView(data.filter { it.parent == 0 },data.size)
        }

    }

    private fun initView() {
        binding.homePageEdit.setOnClickListener {
            activity?.let {
                openImageGallery(it, addLauncher)
            }
        }
        binding.homePageListview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    binding.homePageEdit.hide()
                } else if (dy < 0) {
                    binding.homePageEdit.show()
                }
            }
        })
    }

    private fun initNetData() {
        if (PrefsUtils.isTodayFirstRun(requireContext())) {
            NetGet.getUpdateDetails { updateDetails ->
                lifecycleScope.launch {
                    checkUpdate(updateDetails)
                }
            }
        }
        PrefsUtils.setTodayFirstRunDone(requireContext())
    }

    private fun initAdapter() {
        binding.homePageListview.apply {
            adapter = this@HomeFragment.adapter
            layoutManager = GridLayoutManager(context, 2)
        }
    }

    private fun updateView(data: List<HomePageList>, sum: Int) {
        with(binding) {
            homePageSum.text = if (data.isNotEmpty()) "当前共${data.size}项，${sum}条" else ""
            homePageListview.visibility = if (data.isEmpty()) View.GONE else View.VISIBLE
            homePageAdd.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
            homePageAdd.setOnClickListener {
                activity?.let {
                    openImageGallery(it, addLauncher)
                }
            }
            adapter.updateData(data)
        }
    }

    private fun processResult(result: ActivityResult, onConfirm: (String, String) -> Unit) {
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val editBinding = DialogSingleInputBinding.inflate(layoutInflater)
                editBinding.inputText.apply {
                    hint = "请输入名称"
                }
                val endUrl = moveImage(requireContext(), uri, "homePageList").toString()
                var name = ""
                requireContext().dialog(
                    title = "输入名称",
                    message = "输入的内容最多两行，超出会被省略",
                    positiveButtonText = "确定",
                    negativeButtonText = "取消",
                    onPositive = {
                        name = editBinding.inputText.text.toString()
                        if (name.isNotEmpty()) {
                            onConfirm(name, endUrl)
                        } else {
                            FileUtils.deleteWithPath(endUrl)
                        }
                    },
                    onNegative = {},
                    customView = editBinding.root,
                    onDismiss = {
                        if (name.isEmpty()) {
                            FileUtils.deleteWithPath(endUrl)
                        }
                    }
                )
            }
        } else {
            requireContext().toast("您未选择图片")
        }
    }

    // 用来处理添加事件
    private fun addResult(result: ActivityResult) {
        processResult(result) { name, endUrl ->
            val data = HomePageList(
                System.currentTimeMillis().toString().substring(5).toInt(),
                endUrl,
                name
            )
            viewModel.addData(data)
        }
    }

    // 用来处理编辑事件
    private fun editResult(result: ActivityResult) {
        processResult(result) { name, endUrl ->
            FileUtils.deleteWithPath(currentSelectedData!!.imgUrl)
            val data = currentSelectedData!!.copy(
                name = name,
                imgUrl = endUrl
            )
            viewModel.updateData(data)
        }
    }

    //长按的dialog
    private fun showEditDialog(position: Int, data: HomePageList) {
        currentSelectedData = data
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomBinding = DialogLongClickBinding.inflate(layoutInflater).apply {
            dialogDelete.setOnClickListener {
                showDeleteDialog(position, data)
                bottomSheetDialog.cancel()
            }
            dialogEdit.setOnClickListener {
                openImageGallery(requireActivity(), editLauncher)
                bottomSheetDialog.cancel()
            }
            dialogCancel.setOnClickListener { bottomSheetDialog.cancel() }
        }
        bottomSheetDialog.setContentView(bottomBinding.root)
        bottomSheetDialog.show()
    }

    //删除的dialog
    private fun showDeleteDialog(position: Int, data: HomePageList) {
        requireContext().dialog(
            title = "删除此项",
            message = "你确定要删除这一项嘛？",
            positiveButtonText = "确定",
            onPositive = {
                adapter.removeAt(position)
                viewModel.removeData(data)
                viewModel.removeChildData(data)
                FileUtils.deleteWithPath(data.imgUrl)
            },
            negativeButtonText = "取消",
            onNegative = {}
        )
    }

    //检查升级
    private fun checkUpdate(resource: Resource<UpdateInfo>) {
        when (resource) {
            is Resource.Success -> {
                val updateInfo = resource.data
                val version = NetUtils.getLocalVersion(requireContext())
                if (updateInfo.version != version && updateInfo.updateUrl.isNotEmpty()) {
                    try {
                        requireContext().dialog(
                            title = updateInfo.title,
                            message = updateInfo.message,
                            positiveButtonText = "确定",
                            onPositive = {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(updateInfo.updateUrl)
                                    )
                                )
                            },
                            negativeButtonText = "取消",
                            onNegative = {}
                        )
                    } catch (e: Exception) {
                        LogUtils.e("Get Update Failed: $e")
                    }
                }
            }

            is Resource.Error -> {
                LogUtils.e("Error checking for update: ${resource.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
