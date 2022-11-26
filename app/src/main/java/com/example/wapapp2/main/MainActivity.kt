package com.example.wapapp2.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.wapapp2.R
import com.example.wapapp2.databinding.ActivityMainBinding
import com.example.wapapp2.model.notifications.NotificationType
import com.example.wapapp2.view.calculation.CalcMainFragment
import com.example.wapapp2.view.login.LoginFragment
import com.example.wapapp2.view.main.MainHostFragment
import com.google.firebase.auth.FirebaseUser
import java.net.URLDecoder


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        try {
            //알림 클릭 시 동작
            val arguments = intent.extras
            onNewIntentFromNotification(arguments!!)
        } catch (e: Exception) {
            val loginFragment = LoginFragment()
            supportFragmentManager.beginTransaction().add(R.id.fragment_container_view, loginFragment,
                    LoginFragment::class.java.name).commitAllowingStateLoss()
        }

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    /**
     * 알림 클릭 시 동작
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.apply {
            try {
                onNewIntentFromNotification(extras!!)
            } catch (e: Exception) {

            }
        }
    }

    private fun onNewIntentFromNotification(arguments: Bundle) {
        arguments.apply {
            when (getParcelable<NotificationType>("notificationType")) {
                NotificationType.Chat -> {
                    val calcRoomId = getString("calcRoomId")

                    val fragment = CalcMainFragment()
                    fragment.arguments = Bundle().apply {
                        putString("roomId", calcRoomId)
                    }
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container_view, fragment,
                            CalcMainFragment.TAG).commitAllowingStateLoss()
                }
                else -> {

                }
            }
        }
    }
} 