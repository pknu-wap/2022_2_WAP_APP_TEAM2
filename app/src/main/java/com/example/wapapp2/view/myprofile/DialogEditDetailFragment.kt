package com.example.wapapp2.view.myprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.wapapp2.databinding.DialogEditMyprofileBinding


class DialogEditDetailFragment : DialogFragment() {
    private var _binding: DialogEditMyprofileBinding? = null
    private val binding get() = _binding!!

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}