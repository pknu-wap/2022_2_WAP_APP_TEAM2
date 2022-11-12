package com.example.wapapp2.view.friends


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.R
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.FragmentFriendsBinding
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.view.login.MyFriendsAdapter
import com.example.wapapp2.view.login.Profiles
import com.example.wapapp2.view.myprofile.MyprofileFragment
import com.example.wapapp2.view.main.MainHostFragment
import com.example.wapapp2.viewmodel.FriendsViewModel

class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!
    private val friendsViewModel by viewModels<FriendsViewModel>({ requireActivity() })

    private val friendOnClickListener = ListOnClickListener<FriendDTO> { item, position ->
    }

    private val myFriendsAdapter = MyFriendsAdapter(friendOnClickListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)

        binding.loadingView.setContentView(binding.myFriendsList)

        val myprofile = Profiles(R.drawable.man, "김성윤", "ksu8063@naver.com")

        binding.rvMyprofile.setOnClickListener {
            val fragment = MyprofileFragment()
            val fragmentManager = requireParentFragment().parentFragmentManager
            fragmentManager
                    .beginTransaction()
                    .hide(fragmentManager.findFragmentByTag(MainHostFragment::class.java.name) as Fragment)
                    .add(R.id.fragment_container_view, fragment, "MyprofileFragment")
                    .addToBackStack("MyprofileFragment")
                    .commit()
        }

        binding.ivProfile.setImageResource(myprofile.gender)
        binding.userName1.text = myprofile.name
        binding.userId1.text = myprofile.userid

        myFriendsAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                if (myFriendsAdapter.itemCount > 0) {
                    binding.loadingView.onSuccessful()
                } else {
                    binding.loadingView.onFailed(getString(R.string.empty_my_friends))
                }
            }
        })

        binding.myFriendsList.setHasFixedSize(true)
        binding.myFriendsList.adapter = myFriendsAdapter

        binding.addFriendBtn.setOnClickListener {
            val fragment = AddMyFriendFragment()
            val fragmentManager = requireParentFragment().parentFragmentManager
            fragmentManager
                    .beginTransaction()
                    .hide(fragmentManager.findFragmentByTag(MainHostFragment::class.java.name) as Fragment)
                    .add(R.id.fragment_container_view, fragment, AddMyFriendFragment::class.simpleName)
                    .addToBackStack(AddMyFriendFragment::class.simpleName)
                    .commit()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        friendsViewModel.myFriends.observe(requireActivity()) {
            myFriendsAdapter.friends = it

            friendsViewModel.myFriendsIdSet.clear()
            for (v in it) {
                friendsViewModel.myFriendsIdSet.add(v.friendUserId)
            }
        }
        friendsViewModel.getMyFriends()
    }

}