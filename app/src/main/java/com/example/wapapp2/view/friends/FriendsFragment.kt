package com.example.wapapp2.view.friends


import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.wapapp2.R
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.FragmentFriendsBinding
import com.example.wapapp2.view.bankaccount.MyBankAccountListFragment
import com.example.wapapp2.view.login.ProfileAdapter
import com.example.wapapp2.view.login.Profiles
import com.example.wapapp2.view.main.MainHostFragment

class FriendsFragment : Fragment() {
    private lateinit var viewBinding: FragmentFriendsBinding
    private val friendOnClickListener = ListOnClickListener<Profiles> { item, position ->
        val fragment = MyBankAccountListFragment()
        val fragmentManager = requireParentFragment().parentFragmentManager
        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(MainHostFragment::class.java.name) as
                Fragment)
                .add(R.id.fragment_container_view, fragment, "MyBankAccountListFragment").addToBackStack("MyBankAccountListFragment")
                .commit()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewBinding = FragmentFriendsBinding.inflate(inflater)

        val profileList = arrayListOf(
                Profiles(R.drawable.man, "김성윤", "ksu8063@naver.com"),
                Profiles(R.drawable.man, "박준성", "jesp0305@naver.com"),
                Profiles(R.drawable.girl, "김진우", "nbmlon99@naver.com")
        )

        viewBinding.rvProfile.setHasFixedSize(true)
        viewBinding.rvProfile.adapter = ProfileAdapter(profileList, friendOnClickListener)

        return viewBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}