package com.example.wapapp2.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wapapp2.GrouplistFragment
import com.example.wapapp2.R
import com.example.wapapp2.CalenderFragment
import com.example.wapapp2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val groupFrag = GrouplistFragment()
        val calendereFrag = CalenderFragment()

        supportFragmentManager.beginTransaction().add(R.id.main_fragment, calendereFrag).commit()
    }

    override fun onStart() {
        super.onStart()

        // branch 테스트입니다.
        //1234

    }

    override fun onResume() {
        super.onResume()

    }
}