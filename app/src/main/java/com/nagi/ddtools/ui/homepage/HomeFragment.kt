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
import com.nagi.ddtools.data.Resource
import com.nagi.ddtools.data.UpdateInfo
import com.nagi.ddtools.database.homePagLis.HomePageList
import com.nagi.ddtools.databinding.FragmentHomeBinding
import com.nagi.ddtools.resourceGet.NetGet
import com.nagi.ddtools.ui.adapter.HomePageListAdapter
import com.nagi.ddtools.utils.FileUtils.moveImageIntoAppFile
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
        HomePageListAdapter(mutableListOf()) { position ->
            showDeleteDialog(position)
        }
    }
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        bundle: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                handleActivityResult(result)
            }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNetData()
        initAdapter()
        viewModel.homeListData.observe(viewLifecycleOwner) { data ->
            updateView(data)
        }
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

    private fun updateView(data: List<HomePageList>) {
        with(binding) {
            homePageListview.visibility = if (data.isEmpty()) View.GONE else View.VISIBLE
            homePageAdd.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
            homePageAdd.setOnClickListener {
                activity?.let {
                    openImageGallery(it, resultLauncher)
                }
            }
            adapter.updateData(data)
        }
    }

    private fun handleActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                moveImageIntoAppFile(requireContext(), uri, "homePageList")
            }
        } else {
            requireContext().toast("您未选择图片")
        }
    }
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
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(updateInfo.updateUrl)))
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


    private fun showDeleteDialog(position: Int) {
        requireContext().dialog(
            title = "删除此项",
            message = "你确定要删除这一项嘛？",
            positiveButtonText = "确定",
            onPositive = { adapter.removeAt(position) },
            negativeButtonText = "取消",
            onNegative = {}
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
