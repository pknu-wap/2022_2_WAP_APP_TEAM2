package com.example.wapapp2.view.myprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.wapapp2.databinding.FragmentMyprofileBinding
import com.example.wapapp2.main.MainActivity
import com.example.wapapp2.view.friends.FriendsFragment

class MyprofileFragment : Fragment() {
    private lateinit var viewBinding:FragmentMyprofileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMyprofileBinding.inflate(inflater)
        val mainactivity = activity as MainActivity

        viewBinding.btnConform.setOnClickListener { //확인버튼

            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.remove(this)
                ?.commit()

            mainactivity.gotoFriends()
        }

        viewBinding.btnCancel.setOnClickListener { //취소버튼

            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.remove(this)
                ?.commit()

            mainactivity.gotoFriends()
        }

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}