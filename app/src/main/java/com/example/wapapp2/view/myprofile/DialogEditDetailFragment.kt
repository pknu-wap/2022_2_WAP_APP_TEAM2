package com.example.wapapp2.view.myprofile

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.wapapp2.databinding.DialogEditMyprofileBinding
import com.example.wapapp2.viewmodel.MyAccountViewModel


class DialogEditDetailFragment : DialogFragment() {
    private var _binding: DialogEditMyprofileBinding? = null
    private val binding get() = _binding!!
    private val myAccountViewModel by activityViewModels<MyAccountViewModel>()

    companion object {
        const val TAG = "DialogEditDetailFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): android.view.View? {
        _binding = DialogEditMyprofileBinding.inflate(inflater, container, false)

        binding.button.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myAccountViewModel.myProfileData.observe(viewLifecycleOwner) {
            binding.username.text = it.name.toEditable()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun String.toEditable(): Editable = Editable.Factory().newEditable(this)
}