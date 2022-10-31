package com.example.wapapp2.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wapapp2.R
import com.example.wapapp2.databinding.ActivityMainBinding
import com.example.wapapp2.view.friends.FriendsFragment
import com.example.wapapp2.view.main.MainHostFragment
import com.example.wapapp2.view.main.RootTransactionFragment
import com.example.wapapp2.view.myprofile.MyprofileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rootTransactionFragment = RootTransactionFragment()
        supportFragmentManager.beginTransaction().add(R.id.fragment_container_view, rootTransactionFragment,
                RootTransactionFragment::class.java.name).commitAllowingStateLoss()
    }

    fun gotoMyprofile() {
        val myprofileFragment = MyprofileFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(binding.fragmentContainerView.id, myprofileFragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    fun gotoMain() {
        val rootTransactionFragment = RootTransactionFragment()
        supportFragmentManager.beginTransaction().add(R.id.fragment_container_view, rootTransactionFragment,
            RootTransactionFragment::class.java.name).commitAllowingStateLoss()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

} 