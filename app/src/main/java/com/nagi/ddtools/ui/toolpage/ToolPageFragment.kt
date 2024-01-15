package com.nagi.ddtools.ui.toolpage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nagi.ddtools.databinding.FragmentToolBinding
import com.nagi.ddtools.ui.toolpage.tools.fanboard.FanBoardActivity
import com.nagi.ddtools.ui.toolpage.tools.idolsearch.IdolSearchActivity

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
        initViews()
    }

    private fun initViews() {
        binding.toolSearchIdol.setOnClickListener {
            openPage(requireActivity(), IdolSearchActivity::class.java)
        }
        binding.toolFanBoard.setOnClickListener {
            openPage(requireActivity(), FanBoardActivity::class.java)
        }
    }


    private fun openPage(
        activity: Activity,
        page: Class<*>,
        needClose: Boolean = false,
        bundle: Bundle? = null
    ) {
        val intent = Intent()
        intent.setClass(requireContext(), page)
        intent.extras?.putAll(bundle)
        activity.startActivity(intent)
        if (needClose) activity.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}