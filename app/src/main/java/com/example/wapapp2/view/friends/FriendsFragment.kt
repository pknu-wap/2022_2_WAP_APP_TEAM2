package com.example.wapapp2.view.friends


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wapapp2.R
import com.example.wapapp2.commons.classes.ListAdapterDataObserver
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.FragmentFriendsBinding
import com.example.wapapp2.model.FriendDTO
import com.example.wapapp2.view.friends.adapter.MyFriendsAdapter
import com.example.wapapp2.view.login.Profiles
import com.example.wapapp2.view.myprofile.MyprofileFragment
import com.example.wapapp2.view.main.MainHostFragment
import com.example.wapapp2.viewmodel.FriendsProfileViewModel
import com.example.wapapp2.viewmodel.FriendsViewModel
import com.example.wapapp2.viewmodel.MyAccountViewModel
import com.google.firebase.firestore.EventListener

class FriendsFragment : Fragment() {
    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!
    private val friendsViewModel by activityViewModels<FriendsViewModel>()
    private val myAccountViewModel by activityViewModels<MyAccountViewModel>()
    private val friendsProfileViewModel by viewModels<FriendsProfileViewModel>()

    companion object {
        const val TAG = "FriendsFragment"
    }

    private val friendOnClickListener = ListOnClickListener<FriendDTO> { item, position ->
        val fragment = FriendProfileFragment.newInstance(item)
        fragment.show(childFragmentManager, FriendProfileFragment.TAG)
    }

    private lateinit var myFriendsAdapter: MyFriendsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myFriendsAdapter = MyFriendsAdapter(friendOnClickListener, friendsViewModel.getMyFriendsOptions())

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        binding.loadingView.setContentView(binding.myFriendsList)

        binding.addFriendBtn.setOnClickListener {
            val fragment = AddMyFriendFragment()
            val fragmentManager = requireParentFragment().parentFragmentManager
            fragmentManager
                    .beginTransaction()
                    .hide(fragmentManager.findFragmentByTag(MainHostFragment.TAG) as Fragment)
                    .add(R.id.fragment_container_view, fragment, AddMyFriendFragment.TAG)
                    .addToBackStack(AddMyFriendFragment.TAG)
                    .commit()
        }

        val dataObserver = ListAdapterDataObserver(binding.myFriendsList, binding.myFriendsList.layoutManager as
                LinearLayoutManager, myFriendsAdapter)
        dataObserver.registerLoadingView(binding.loadingView, getString(R.string.empty_my_friends))
        dataObserver.onChanged()

        myFriendsAdapter.registerAdapterDataObserver(dataObserver)
        binding.myFriendsList.adapter = myFriendsAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMyProfile()
        setFriendsProfile()
    }


    override fun onStart() {
        super.onStart()
        myFriendsAdapter.startListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        myFriendsAdapter.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun setMyProfile() {
        binding.rvMyprofile.setOnClickListener {
            val fragment = MyprofileFragment()
            val fragmentManager = requireParentFragment().parentFragmentManager
            fragmentManager
                    .beginTransaction()
                    .hide(fragmentManager.findFragmentByTag(MainHostFragment.TAG) as Fragment)
                    .add(R.id.fragment_container_view, fragment, MyprofileFragment.TAG)
                    .addToBackStack(MyprofileFragment.TAG)
                    .commit()
        }

        myAccountViewModel.myProfileData.observe(viewLifecycleOwner) {
            if(it.imgUri.isEmpty().not())
                Glide.with(binding.root).load(it.imgUri).circleCrop().into(binding.myProfileImg)
            else if(it.gender == "man")
                binding.myProfileImg.setImageDrawable(ContextCompat.getDrawable(requireContext() ,R.drawable.man))
            else
                binding.myProfileImg.setImageDrawable(ContextCompat.getDrawable(requireContext() ,R.drawable.girl))

            binding.myProfileName.text = it.name
            binding.myAccountId.text = it.email
        }

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            myFriendsAdapter.stopListening()
        } else {
            myFriendsAdapter.startListening()
        }
    }


    private fun setFriendsProfile() {
        friendsViewModel.friendsListLiveData.observe(viewLifecycleOwner){
            it.forEach {
                friendsProfileViewModel.loadFriendProfile(it.friendUserId, EventListener { userDTO, error ->
                    if(userDTO != null)
                        myFriendsAdapter.getItemViewHolder(userDTO!!.id)!!.updateProfileImg(requireContext(), userDTO["imgUri"].toString(), userDTO["gender"].toString())
                })
            }
        }
    }
}