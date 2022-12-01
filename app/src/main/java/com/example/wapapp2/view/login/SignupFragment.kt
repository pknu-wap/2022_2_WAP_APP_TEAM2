package com.example.wapapp2.view.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.wapapp2.R
import com.example.wapapp2.databinding.FragmentSignupBinding
import com.example.wapapp2.model.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupFragment : Fragment() {
    private var _viewBinding: FragmentSignupBinding? = null
    private val viewBinding get() = _viewBinding!!
    var db: FirebaseFirestore? = null

    companion object {
        val TAG = "SignupFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        _viewBinding = FragmentSignupBinding.inflate(inflater, container, false)

        val auth = FirebaseAuth.getInstance()

        fun moveLoginPage() {
            val loginFragment = LoginFragment()

            parentFragmentManager
                    .beginTransaction()
                    .hide(this@SignupFragment)
                    .add(R.id.fragment_container_view, loginFragment, LoginFragment.TAG)
                    .commitAllowingStateLoss()
        }

        fun UpdateProfile(uid: String) {
            db = FirebaseFirestore.getInstance()
            var gender: String = "man"

            viewBinding.radioGroupGender.setOnCheckedChangeListener { radioGroup, checkedID ->
                when (checkedID) {
                    viewBinding.genderMan.id -> gender = "man"
                    viewBinding.genderGirl.id -> gender = "girl"
                }
            }

            val userDTO =
                    UserDTO(
                            id = uid, email = viewBinding.userId.text.toString(), gender = gender, imgUri = "", name = viewBinding.userName
                            .text.toString(), "", arrayListOf(),
                            arrayListOf(), arrayListOf(), arrayListOf())

            db!!.collection("users").document(uid).set(userDTO)
        }

        fun createEmail() {
            if (viewBinding.userId.text.toString().isNullOrEmpty() ||
                    viewBinding.userPassword.text.toString().isNullOrEmpty()) {
                Toast.makeText(context,
                        "이메일과 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show()
            } else {
                if (viewBinding.userPassword.text.toString() == viewBinding.userPasswordCheck.text.toString()) {
                    auth?.createUserWithEmailAndPassword(viewBinding.userId.text.toString(), viewBinding.userPassword.text.toString())
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val id = task.result.user?.uid
                                    Toast.makeText(context,
                                            "회원가입이 완료 되었습니다.", Toast.LENGTH_SHORT).show()

                                    UpdateProfile(id!!)
                                    moveLoginPage()
                                } else {
                                    Toast.makeText(context,
                                            "이미 가입된 이메일입니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                } else {
                    Toast.makeText(context,
                            "비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewBinding.btnSignup.setOnClickListener { createEmail() }
        viewBinding.btnLogin.setOnClickListener { moveLoginPage() }

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

}