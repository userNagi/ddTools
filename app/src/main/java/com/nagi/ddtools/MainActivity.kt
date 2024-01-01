package com.nagi.ddtools

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.nagi.ddtools.databinding.ActivityMainBinding
import com.nagi.ddtools.resourceGet.NetGet
import com.nagi.ddtools.utils.NetUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        setupStatusBar()
        setupNavigation()
        checkUpdate()
    }

    private fun checkUpdate() {
        val updateInfo = NetGet.getUpdateDetails(applicationContext)
        val version = NetUtils.getLocalVersion(applicationContext)
        if (updateInfo.first != version && updateInfo.second.isNotEmpty()) {
        }
    }

    private fun setupStatusBar() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.lty)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as? NavHostFragment
            ?: return

        val navController = navHostFragment.navController
        binding.navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.navTitleText.text = destination.label
        }
    }
}