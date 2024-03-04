package com.nagi.ddtools.ui.minepage

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nagi.ddtools.R
import com.nagi.ddtools.database.user.User
import com.nagi.ddtools.databinding.FragmentMineBinding
import com.nagi.ddtools.ui.minepage.about.AboutActivity
import com.nagi.ddtools.ui.minepage.feedback.UserFeedBack
import com.nagi.ddtools.ui.minepage.join.JoinUsActivity
import com.nagi.ddtools.ui.minepage.setting.GlobalSettingActivity
import com.nagi.ddtools.ui.minepage.user.login.LoginActivity
import com.nagi.ddtools.ui.minepage.user.userInfo.UserInfoActivity
import com.nagi.ddtools.utils.UiUtils.openPage

class MinePageFragment : Fragment() {

    private var _binding: FragmentMineBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is error")
    private val viewModel: MinePageViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        viewModel.user.observe(viewLifecycleOwner) { setUserStatus(it) }
    }

    override fun onResume() {
        super.onResume()
        viewModel.reGetUser()
    }

    private fun initView() {
        binding.apply {
            mineLinearCollect.apply {
                setData(R.drawable.ic_collections, "我的收藏", "red")
            }
            mineLinearFeedback.apply {
                setData(R.drawable.ic_feedback, "用户反馈", "gray")
                setOnClickListener(requireActivity(), UserFeedBack::class.java)
            }
            mineLinearJoinUs.apply {
                setData(R.drawable.ic_join_us, "加入我们", "blue")
                setOnClickListener(requireActivity(), JoinUsActivity::class.java)
            }
            mineLinearSetting.apply {
                setData(R.drawable.ic_mine_setting, "全局设置", "lty")
                setOnClickListener(requireActivity(), GlobalSettingActivity::class.java)
            }
            mineLinearAbout.apply {
                setData(R.drawable.ic_about, "关于", "green")
                setOnClickListener(requireActivity(), AboutActivity::class.java)
            }
            loginViewImage.setOnClickListener {
                if (viewModel.user.value.isNullOrEmpty()) openPage(
                    requireActivity(), LoginActivity::class.java
                ) else openPage(requireActivity(), UserInfoActivity::class.java)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUserStatus(user: List<User>) {
        if (user.isNotEmpty()) {
            user.first().let { userInfo ->
                binding.apply {
                    loginNickname.text = userInfo.nickname
                    loginUsername.text = "用户名：${userInfo.username}"
                    if (userInfo.avatar_url.isNotEmpty() && userInfo.avatar_url.isNotBlank()) {
                        loginViewImage.onCLick(userInfo.avatar_url)
                        loginViewImage.setImageUrl(userInfo.avatar_url)
                    }
                }
            }
        } else {
            binding.loginUsername.text = ""
            binding.loginNickname.text = "点击头像去登录"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}