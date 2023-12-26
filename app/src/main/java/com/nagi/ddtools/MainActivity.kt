package com.nagi.ddtools

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nagi.ddtools.database.AppDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GlobalScope.launch {
            AppDatabase.getInstance(applicationContext).homePageDao().getAll()
        }
    }
}