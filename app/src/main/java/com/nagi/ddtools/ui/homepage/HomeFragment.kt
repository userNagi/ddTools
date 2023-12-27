package com.nagi.ddtools.ui.homepage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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

    private val viewModel: HomeViewModel by viewModels()

    private var resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                moveImageIntoAppFile(requireContext(), uri, "homePageList")
            }
        } else {
            toast(requireContext(), "您未选择图片")
        }
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
        viewModel.homeListData.observe(viewLifecycleOwner) { data ->
            updateView(data)
        }
    }

    private fun updateView(data: List<HomePageList>) {
        if (data.isEmpty()) {
            binding.homePageListview.visibility = View.GONE
            binding.homePageAdd.visibility = View.VISIBLE
            binding.homePageAdd.setOnClickListener {
                activity?.let {
                    openImageGallery(it, resultLauncher)
                }
            }
            return
        }
        binding.homePageListview.visibility = View.VISIBLE
        binding.homePageAdd.visibility = View.GONE
        val adapter = HomePageListAdapter(data)
        binding.homePageListview.adapter = adapter
        binding.homePageListview.layoutManager = GridLayoutManager(context, 2)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}