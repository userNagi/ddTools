package com.nagi.ddtools.ui.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.nagi.ddtools.database.homePagLis.HomePageList
import com.nagi.ddtools.databinding.FragmentHomeBinding
import com.nagi.ddtools.ui.adapter.HomePageListAdapter

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

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
//            return
        }
        val adapterTest = HomePageListAdapter(listOf(
            HomePageList(1,"111","111"),
            HomePageList(1,"111","111"),
            HomePageList(1,"111","111"),
            HomePageList(1,"111","111"),
            HomePageList(1,"111","111"),
            HomePageList(1,"111","111"),
            HomePageList(1,"111","111"),
            HomePageList(1,"111","111"),
            HomePageList(1,"111","111"),
            HomePageList(1,"111","111"),
            HomePageList(1,"111","111"),
            HomePageList(1,"111","111"),
        ))
        val adapter = HomePageListAdapter(data)
        binding.homePageListview.adapter = adapterTest
        binding.homePageListview.layoutManager = GridLayoutManager(context,2)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding?.let {
            _binding = null
        }
    }
}
