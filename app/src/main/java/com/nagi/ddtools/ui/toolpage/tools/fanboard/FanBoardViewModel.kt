package com.nagi.ddtools.ui.toolpage.tools.fanboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FanBoardViewModel : ViewModel() {
    // 使用LiveData来表示开关状态，初始值为false
    private val _isRolling = MutableLiveData<Boolean>(false)
    val isRolling: LiveData<Boolean> = _isRolling

    // 切换滚动状态
    fun toggleRolling() {
        _isRolling.value = _isRolling.value != true // 当前值的非
    }
}
