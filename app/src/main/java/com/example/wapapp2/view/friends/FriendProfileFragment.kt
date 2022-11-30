package com.example.wapapp2.view.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.wapapp2.R
import com.example.wapapp2.databinding.FragmentFriendProfileBinding
import com.example.wapapp2.model.UserDTO
import com.example.wapapp2.viewmodel.FriendAliasViewModel
import com.example.wapapp2.viewmodel.FriendsViewModel
import com.example.wapapp2.viewmodel.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class FriendProfileFragment : DialogFragment() {
    private var _binding: FragmentFriendProfileBinding? = null
    private val binding get() = _binding!!

    private val friendsViewModel by activityViewModels<FriendsViewModel>()
    private val friendAliasViewModel by viewModels<FriendAliasViewModel>()
    private val userViewModel by viewModels<UserViewModel>()
    private var showModifyAliasLayout = false

    lateinit var dstUserDTO: UserDTO

    companion object {
        const val TAG = "FriendProfileFragment"

        fun newInstance(userDTO: UserDTO): FriendProfileFragment {
            val fragment = FriendProfileFragment()
            fragment.arguments = Bundle().apply {
                putParcelable("userDTO", userDTO)
            }
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        arguments?.apply {
            dstUserDTO = getParcelable("userDTO")!!
            friendAliasViewModel.friendDTO = FriendsViewModel.MY_FRIEND_MAP[dstUserDTO.id]
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFriendProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.setAliasLayout.visibility = View.GONE
        binding.username.text = friendAliasViewModel.friendDTO?.alias

        binding.aliasBtn.setOnClickListener {
            binding.setAliasLayout.visibility = if (showModifyAliasLayout) View.GONE else View.VISIBLE
            showModifyAliasLayout = !showModifyAliasLayout
        }

        binding.saveAliasBtn.setOnClickListener {
            if (binding.aliasEditText.text.isNullOrEmpty()) {
                Toast.makeText(requireContext().applicationContext, R.string.empty_alias, Toast.LENGTH_SHORT).show()
            } else {
                val alias = binding.aliasEditText.text.toString()
                val msg = "${alias}로 설정하시겠습니까?"

                MaterialAlertDialogBuilder(requireActivity())
                        .setTitle(R.string.set_alias)
                        .setMessage(msg)
                        .setNegativeButton(R.string.cancel) { dialog, which ->
                            dialog.dismiss()
                        }.setPositiveButton(R.string.check) { dialog, which ->
                            friendAliasViewModel.setAliasToMyFriend(alias, friendAliasViewModel.friendDTO!!.friendUserId,
                                    friendAliasViewModel.friendDTO!!.email)
                            dialog.dismiss()
                            dismiss()
                        }.create().show()
            }
        }

        binding.removeBtn.setOnClickListener {
            val msg = "${friendAliasViewModel.friendDTO?.alias} 삭제하시겠습니까?"
            MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(R.string.remove)
                    .setMessage(msg)
                    .setNegativeButton(R.string.cancel) { dialog, which ->
                        dialog.dismiss()
                    }.setPositiveButton(R.string.remove) { dialog, which ->
                        friendsViewModel.removeMyFriend(friendAliasViewModel.friendDTO!!.friendUserId)
                        dialog.dismiss()
                        dismiss()
                    }.create().show()
        }


        userViewModel.user.observe(viewLifecycleOwner) {
            if (it != null) {
                val text = "실제 이름 : ${it.name}"
                binding.realUserName.text = text
            } else {
                binding.realUserName.text = getString(R.string.error_load_real_name_of_user)
            }
        }
        userViewModel.getUser(friendAliasViewModel.friendDTO!!.friendUserId)

        if (dstUserDTO.imgUri.isEmpty().not()) {
            Glide.with(binding.root).load(dstUserDTO.imgUri).centerCrop().into(binding.profile)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("friendDTO", friendAliasViewModel.friendDTO)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}