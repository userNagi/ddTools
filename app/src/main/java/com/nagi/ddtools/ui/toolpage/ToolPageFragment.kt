package com.nagi.ddtools.ui.toolpage

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nagi.ddtools.databinding.DialogChooseSelectedWayBinding
import com.nagi.ddtools.databinding.DialogSingleInputBinding
import com.nagi.ddtools.databinding.FragmentToolBinding
import com.nagi.ddtools.ui.toolpage.tools.activitysearch.ActivitySearchActivity
import com.nagi.ddtools.ui.toolpage.tools.fanboard.FanBoardActivity
import com.nagi.ddtools.ui.toolpage.tools.groupwho.ChooseWhoActivity
import com.nagi.ddtools.ui.toolpage.tools.idolsearch.IdolSearchActivity
import com.nagi.ddtools.ui.widget.dialog.IncludeFromActivityDialog
import com.nagi.ddtools.utils.UiUtils.dialog
import com.nagi.ddtools.utils.UiUtils.hideDialog
import com.nagi.ddtools.utils.UiUtils.openPage

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
        viewModel.title.observe(viewLifecycleOwner) {
            binding.toolMainBody.text = it
        }
        initViews()
    }

    private fun initViews() {
        binding.toolMainBody.setOnClickListener {
            val editBinding =
                DialogSingleInputBinding.inflate(layoutInflater)
            editBinding.inputText.apply {
                setText(viewModel.title.value)
            }
            requireContext().dialog(
                title = "",
                message = "",
                positiveButtonText = "确定",
                negativeButtonText = "取消",
                onPositive = {
                    val newTitle = editBinding.inputText.text.toString()
                    viewModel.setTitle(newTitle)
                },
                onNegative = {
                },
                customView = editBinding.root
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
            requireActivity().dialog(
                "提示",
                "暂不可用（主要是没有图）请耐心等待（找到图再开放），有图可以通过反馈联系",
                "确定",
                "取消"
            )
//            openPage(requireActivity(), ChoosePostureActivity::class.java)
        }
        binding.toolFanBoard.setOnClickListener {
            openPage(requireActivity(), FanBoardActivity::class.java)
        }
    }

    private fun showIncludeDialog() {
        val dialogBinding = DialogChooseSelectedWayBinding.inflate(LayoutInflater.from(context))
        dialogBinding.importFromActivity.apply {
            text = "从活动导入"
            setOnClickListener {
                hideDialog()
                showIncludeFromActivity()
            }
        }
        dialogBinding.importFromCustom.apply {
            text = "自定义导入"
            setOnClickListener {
                openPage(requireActivity(), ChooseWhoActivity::class.java, false)
            }
        }
        requireContext().dialog(
            "选择", "请选择，使用活动导入需要选择活动", "",
            "", customView = dialogBinding.root
        )
    }

    private fun showIncludeFromActivity() {
        val dialogFragment = IncludeFromActivityDialog()
        dialogFragment.show(childFragmentManager, "customDialog")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}