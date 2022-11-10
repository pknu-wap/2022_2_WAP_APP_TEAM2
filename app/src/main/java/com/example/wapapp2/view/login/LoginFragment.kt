package com.example.wapapp2.view.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.wapapp2.R
import com.example.wapapp2.databinding.FragmentLoginBinding
import com.example.wapapp2.view.main.RootTransactionFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    var auth: FirebaseAuth? = null

    override fun onStart() {
        super.onStart()

        moveMainPage(auth?.currentUser)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
    }

    fun emailLogin() {
        if(binding.emailEdittext.text.toString().isNullOrEmpty() ||
                binding.passwordEdittext.text.toString().isNullOrEmpty()) {
            Toast.makeText(context,
                "이메일과 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show()
        } else {
            signinEmail()
        }
    }

    fun signinEmail() {
        auth?.signInWithEmailAndPassword(binding.emailEdittext.text.toString(), binding.passwordEdittext.text.toString())
            ?.addOnCompleteListener { task ->
                if(task.isSuccessful){
                    moveMainPage(auth?.currentUser)
                } else {
                    Toast.makeText(context, "이메일이나 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun moveMainPage(user : FirebaseUser?){
        if (user != null) {
            val rootTransactionFragment = RootTransactionFragment()

            parentFragmentManager
                .beginTransaction()
                .hide(this@LoginFragment)
                .add(R.id.fragment_container_view, rootTransactionFragment, RootTransactionFragment::class.java.name)
                .commitAllowingStateLoss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater)

        binding.btnLogin.setOnClickListener { emailLogin() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}