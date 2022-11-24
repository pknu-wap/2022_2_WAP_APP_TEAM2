package com.example.wapapp2.main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wapapp2.R
import com.example.wapapp2.databinding.ActivityMainBinding
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