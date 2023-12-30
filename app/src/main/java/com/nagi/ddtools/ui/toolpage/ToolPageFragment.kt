package com.nagi.ddtools.ui.toolpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nagi.ddtools.databinding.FragmentToolBinding

class ToolPageFragment : Fragment() {

    private var _binding: FragmentToolBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ToolPageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        bundle: Bundle?
    ): View {
        _binding = FragmentToolBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}