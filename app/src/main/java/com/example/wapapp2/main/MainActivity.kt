package com.example.wapapp2.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.wapapp2.R
import com.example.wapapp2.databinding.ActivityMainBinding
import com.example.wapapp2.repository.AppCheckRepository
import com.example.wapapp2.repository.FriendsRepository
import com.example.wapapp2.repository.ReceiptImgRepositoryImpl
import com.example.wapapp2.repository.ReceiptRepositoryImpl
import com.example.wapapp2.view.main.RootTransactionFragment
import com.example.wapapp2.viewmodel.AccountSignViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val accountSignViewModel by viewModels<AccountSignViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rootTransactionFragment = RootTransactionFragment()
        supportFragmentManager.beginTransaction().add(R.id.fragment_container_view, rootTransactionFragment,
                RootTransactionFragment::class.java.name).commitAllowingStateLoss()


        //test용으로 naversovc@gmail.com로 자동로그인
        val auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            onSignIn(auth.currentUser!!)
        } else {
            auth.signInWithEmailAndPassword("naversovc@gmail.com", "@google0909")
                    .addOnSuccessListener {
                        onSignIn(it.user!!)
                    }.addOnFailureListener {
                        Toast.makeText(this, "naversovc@gmail.com 로그인 실패", Toast.LENGTH_SHORT).show()
                        accountSignViewModel.isSignIn = false
                        accountSignViewModel.signInUser = null
                    }
        }
    }

    private fun onSignIn(user: FirebaseUser) {
        accountSignViewModel.signInUser = user
        accountSignViewModel.isSignIn = true
        Toast.makeText(this, "naversovc@gmail.com 로그인 완료", Toast.LENGTH_SHORT).show()

        ReceiptImgRepositoryImpl.initialize()
        ReceiptRepositoryImpl.initialize()
        AppCheckRepository.initialize()
        FriendsRepository.initialize()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

} 