package com.nagi.ddtools.ui.homepage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.nagi.ddtools.database.homePagLis.HomePageList
import com.nagi.ddtools.databinding.FragmentHomeBinding
import com.nagi.ddtools.ui.adapter.HomePageListAdapter
import com.nagi.ddtools.utils.FileUtils.moveImageIntoAppFile
import com.nagi.ddtools.utils.FileUtils.openImageGallery
import com.nagi.ddtools.utils.UiUtils.toast

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: HomePageListAdapter
    private var isAdapterInitialized = false
    private val viewModel: HomeViewModel by viewModels()

    private var resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleActivityResult(result)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        bundle: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        viewModel.homeListData.observe(viewLifecycleOwner) { data ->
            updateView(data)
        }
    }
    private fun initAdapter() {
        if (!isAdapterInitialized) {
            adapter = HomePageListAdapter(mutableListOf()) { position ->
                showDeleteDialog(position)
            }
            binding.homePageListview.adapter = adapter
            binding.homePageListview.layoutManager = GridLayoutManager(context, 2)
            isAdapterInitialized = true
        }
    }
    private fun updateView(data: List<HomePageList>) {
        binding.homePageListview.visibility = if (data.isEmpty()) View.GONE else View.VISIBLE
        binding.homePageAdd.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE

        if (data.isEmpty()) {
            binding.homePageAdd.setOnClickListener {
                openImageGallery()
            }
        } else {
            adapter.updateData(data)
        }
    }
    private fun openImageGallery() {
        activity?.let { openImageGallery(it, resultLauncher) }
    }
    private fun handleActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                moveImageIntoAppFile(requireContext(), uri, "homePageList")
            }
        } else {
            toast(requireContext(),"您未选择图片")
        }
    }

    private fun showDeleteDialog(position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("删除此项")
            .setMessage("你确定要删除这一项嘛")
            .setPositiveButton("确定") { dialog, _ ->
                adapter.removeAt(position)
                dialog.dismiss()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
