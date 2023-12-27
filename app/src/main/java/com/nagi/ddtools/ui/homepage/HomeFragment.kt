package com.nagi.ddtools.ui.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.nagi.ddtools.database.homePagLis.HomePageList
import com.nagi.ddtools.databinding.FragmentHomeBinding

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
        // TODO: 实现此方法以使用提供的数据更新视图
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding?.let {
            _binding = null
        }
    }
}
