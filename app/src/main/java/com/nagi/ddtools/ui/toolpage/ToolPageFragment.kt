package com.nagi.ddtools.ui.toolpage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nagi.ddtools.databinding.DialogChooseSelectedWayBinding
import com.nagi.ddtools.databinding.FragmentToolBinding
import com.nagi.ddtools.ui.toolpage.tools.activitysearch.ActivitySearchActivity
import com.nagi.ddtools.ui.toolpage.tools.fanboard.FanBoardActivity
import com.nagi.ddtools.ui.toolpage.tools.idolsearch.IdolSearchActivity
import com.nagi.ddtools.utils.UiUtils.dialog

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
        initViewModel()
        binding.toolMainBody.setOnClickListener {
            val editText = EditText(requireContext()).apply {
                gravity = Gravity.CENTER
                setText(viewModel.title.value)
            }
            requireContext().dialog(
                title = "",
                message = "",
                positiveButtonText = "确定",
                negativeButtonText = "取消",
                onPositive = {
                    val newTitle = editText.text.toString()
                    viewModel.setTitle(newTitle)
                },
                onNegative = {
                },
                customView = editText
            )
        }
        binding.toolSearchIdol.setOnClickListener {
            openPage(requireActivity(), IdolSearchActivity::class.java)
        }
        binding.toolSearchActivity.setOnClickListener {
            openPage(requireActivity(), ActivitySearchActivity::class.java)
        }
        binding.toolChooseWho.setOnClickListener {
            showIncludeDialog()
        }
        binding.toolChoosePosture.setOnClickListener {

        }
        binding.toolFanBoard.setOnClickListener {
            openPage(requireActivity(), FanBoardActivity::class.java)
        }
    }

    private fun initViewModel() {
        viewModel.title.observe(viewLifecycleOwner) {
            binding.toolMainBody.text = it
        }
    }

    private fun showIncludeDialog() {
        val dialogBinding = DialogChooseSelectedWayBinding.inflate(LayoutInflater.from(context))
        dialogBinding.importFromActivity.apply {
            text = "从活动导入"
        }
        dialogBinding.importFromCustom.apply {
            text = "自定义导入"
        }
        requireContext().dialog(
            title = "选择",
            message = "选择",
            positiveButtonText = "",
            negativeButtonText = "",
            onPositive = {
            },
            onNegative = {
            },
            customView = dialogBinding.root

        )
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