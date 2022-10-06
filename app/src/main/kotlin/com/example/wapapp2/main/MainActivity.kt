package com.example.wapapp2.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wapapp2.R
import com.example.wapapp2.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        // 2115
    }

    override fun onResume() {
        super.onResume()

    }
}