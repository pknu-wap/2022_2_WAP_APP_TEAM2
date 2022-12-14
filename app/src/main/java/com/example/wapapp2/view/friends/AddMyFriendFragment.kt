package com.example.wapapp2.view.friends

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.wapapp2.R
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.FragmentAddMyFriendBinding
import com.example.wapapp2.model.UserDTO
import com.example.wapapp2.view.friends.adapter.SearchUserListAdapter
import com.example.wapapp2.viewmodel.FriendsViewModel
import com.example.wapapp2.viewmodel.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class AddMyFriendFragment : Fragment() {
    private var _binding: FragmentAddMyFriendBinding? = null
    private val binding get() = _binding!!

    private val friendsViewModel by activityViewModels<FriendsViewModel>()
    private val userViewModel by viewModels<UserViewModel>()

    private val listOnClickListener = ListOnClickListener<UserDTO> { item, pos ->
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.add_friend)
                .setMessage("${item.name} 추가하시겠습니까?")
                .setPositiveButton(R.string.add) { dialog, which ->
                    dialog.dismiss()
                    friendsViewModel.addToMyFriend(item)
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }.setNeutralButton(R.string.cancel) { dialog, which ->
                    dialog.dismiss()
                }.create().show()
    }
    private val adapter = SearchUserListAdapter(listOnClickListener)

    companion object {
        val TAG = "AddMyFriend"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel.myFriendsIdSet.addAll(friendsViewModel.friendsMap.value!!.keys)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAddMyFriendBinding.inflate(inflater, container, false)
        binding.searchUserList.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.searchUserList.adapter = adapter

        binding.loadingView.setContentView(getString(R.string.no_search_results_found), binding.searchUserList)

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel.searchUsersResult.observe(viewLifecycleOwner) {
            adapter.users = it

            if (it.isEmpty())
                binding.loadingView.onFailed(getString(R.string.no_search_results_found))
            else
                binding.loadingView.onSuccessful()
        }


        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    binding.loadingView.onStarted()
                    userViewModel.findUsers(query)
                } else
                    Toast.makeText(context, R.string.empty_search_query, Toast.LENGTH_SHORT).show()

                return true
            }

            override fun onQueryTextChange(newText: String?) = true
        })

    }


}