package com.example.wapapp2.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.wapapp2.R
import com.example.wapapp2.databinding.ActivityMainBinding
import com.example.wapapp2.model.notifications.NotificationType
import com.example.wapapp2.view.calculation.CalcMainFragment
import com.example.wapapp2.view.login.LoginFragment
import com.example.wapapp2.view.main.RootTransactionFragment
import com.example.wapapp2.viewmodel.MyAccountViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val myAccountViewModel by viewModels<MyAccountViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            //알림 클릭 시 동작
            val arguments = intent.extras!!
            if (arguments.isEmpty)
                nonClickedNotification()
            else
                onIntentFromNotification(arguments)
        } catch (e: Exception) {
            nonClickedNotification()
        }

    }

    private fun nonClickedNotification() {
        //로그인 여부 확인
        if (myAccountViewModel.onSignIn()) {
            val rootTransactionFragment = RootTransactionFragment()
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container_view, rootTransactionFragment,
                    RootTransactionFragment.TAG).commit()
        } else {
            val loginFragment = LoginFragment()
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container_view, loginFragment,
                    LoginFragment.TAG).commit()
        }

    }

    override fun onRestart() {
        super.onRestart()
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
                onIntentFromNotification(extras!!)
            } catch (e: Exception) {
                nonClickedNotification()
            }
        }
    }

    private fun onIntentFromNotification(arguments: Bundle) {
        arguments.apply {
            if (!containsKey("notificationType")) {
                nonClickedNotification()
                return
            }

            when (getParcelable<NotificationType>("notificationType")) {
                NotificationType.Chat -> {
                    val calcRoomId = getString("calcRoomId")

                    val fragment = CalcMainFragment()
                    fragment.arguments = Bundle().apply {
                        putString("roomId", calcRoomId)
                    }

                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container_view, fragment,
                            CalcMainFragment.TAG).commit()
                }
                else -> {

                }
            }
        }
    }

}