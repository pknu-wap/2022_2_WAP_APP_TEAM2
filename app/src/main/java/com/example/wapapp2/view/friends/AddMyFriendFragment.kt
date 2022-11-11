package com.example.wapapp2.view.friends

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.wapapp2.R
import com.example.wapapp2.commons.classes.DelayTextWatcher
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.FragmentAddMyFriendBinding
import com.example.wapapp2.model.UserDTO
import com.example.wapapp2.view.friends.adapter.SearchUserListAdapter
import com.example.wapapp2.viewmodel.FriendsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class AddMyFriendFragment : Fragment() {
    private var _binding: FragmentAddMyFriendBinding? = null
    private val binding get() = _binding!!

    private val friendsViewModel by viewModels<FriendsViewModel>()

    private val listOnClickListener = ListOnClickListener<UserDTO> { item, pos ->
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.add_friend)
                .setMessage("${item.name} 추가하시겠습니까?")
                .setPositiveButton(R.string.add) { dialog, which ->
                    dialog.dismiss()
                    //추가로직

                }.setNeutralButton(R.string.cancel) { dialog, which ->
                    dialog.dismiss()

                }.create().show()
    }
    private val adapter = SearchUserListAdapter(listOnClickListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentAddMyFriendBinding.inflate(inflater, container, false)
        binding.searchUserList.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.searchUserList.adapter = adapter
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        friendsViewModel.searchUsersResult.observe(viewLifecycleOwner) {
            adapter.users = it
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    friendsViewModel.findUsers(query)
                } else {
                    Toast.makeText(context, R.string.empty_search_query, Toast.LENGTH_SHORT).show()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?) = true
        })
    }
}