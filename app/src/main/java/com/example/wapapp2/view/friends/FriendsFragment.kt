package com.example.wapapp2.view.friends


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.R
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.FragmentFriendsBinding
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.view.friends.adapter.MyFriendsAdapter
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

    private lateinit var myFriendsAdapter: MyFriendsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myFriendsAdapter = MyFriendsAdapter(friendOnClickListener, friendsViewModel.getMyFriendsOptions())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        binding.loadingView.setContentView(binding.myFriendsList)
        binding.loadingView.onSuccessful()

        val myprofile = Profiles(R.drawable.man, "김성윤", "ksu8063@naver.com")

        binding.rvMyprofile.setOnClickListener {
            val fragment = MyprofileFragment()
            val fragmentManager = requireParentFragment().parentFragmentManager
            fragmentManager
                    .beginTransaction()
                    .hide(fragmentManager.findFragmentByTag(MainHostFragment::class.java.name) as Fragment)
                    .add(R.id.fragment_container_view, fragment, MyprofileFragment.TAG)
                    .addToBackStack(MyprofileFragment.TAG)
                    .commit()
        }

        binding.ivProfile.setImageResource(myprofile.gender)
        binding.userName1.text = myprofile.name
        binding.userId1.text = myprofile.userid

        binding.addFriendBtn.setOnClickListener {
            val fragment = AddMyFriendFragment()
            val fragmentManager = requireParentFragment().parentFragmentManager
            fragmentManager
                    .beginTransaction()
                    .hide(fragmentManager.findFragmentByTag(MainHostFragment::class.java.name) as Fragment)
                    .add(R.id.fragment_container_view, fragment, AddMyFriendFragment.TAG)
                    .addToBackStack(AddMyFriendFragment.TAG)
                    .commit()
        }

        binding.myFriendsList.adapter = myFriendsAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        myFriendsAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        myFriendsAdapter.stopListening()
    }


}