package com.nagi.ddtools.ui.minepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nagi.ddtools.databinding.FragmentMineBinding
import com.nagi.ddtools.ui.minepage.login.LoginActivity
import com.nagi.ddtools.utils.UiUtils.openPage

class MinePageFragment : Fragment() {

    private var _binding: FragmentMineBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this)[MinePageViewModel::class.java]

        _binding = FragmentMineBinding.inflate(inflater, container, false)
        val root: View = binding.root

        notificationsViewModel.text.observe(viewLifecycleOwner) {

        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.loginViewImage.setOnClickListener {
            requireActivity().openPage(requireActivity(),LoginActivity::class.java)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}