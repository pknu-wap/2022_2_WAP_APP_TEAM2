package com.example.wapapp2.view.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.wapapp2.databinding.FragmentEditPasswordBinding
import com.example.wapapp2.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class EditPasswordFragment: Fragment() {
    private var _binding: FragmentEditPasswordBinding? = null
    private val binding get() = _binding!!
    var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (!parentFragmentManager.popBackStackImmediate()) {
                requireActivity().finish()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditPasswordBinding.inflate(inflater, container, false)

        fun send() {
            var email = binding.edittextEmail.getText().toString()

            if (email.length > 0) {
                auth!!.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "이메일을 보냈습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(context, "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSend.setOnClickListener { send() }
        binding.btnCancel.setOnClickListener {
            parentFragmentManager
                .popBackStackImmediate()
        }

        return binding.root
    }

    override fun onDestroy() {
        onBackPressedCallback.remove()
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}