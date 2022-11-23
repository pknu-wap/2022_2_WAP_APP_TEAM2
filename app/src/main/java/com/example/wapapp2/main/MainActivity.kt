package com.example.wapapp2.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.wapapp2.R
import com.example.wapapp2.databinding.ActivityMainBinding
import com.example.wapapp2.service.MyFirebaseMessagingService

import com.example.wapapp2.view.login.LoginFragment

import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val loginFragment = LoginFragment()
        supportFragmentManager.beginTransaction().add(R.id.fragment_container_view, loginFragment,
                LoginFragment::class.java.name).commitAllowingStateLoss()
    }

    private fun onSignIn(user: FirebaseUser) {
        Toast.makeText(this, "naversovc@gmail.com 로그인 완료", Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

} 