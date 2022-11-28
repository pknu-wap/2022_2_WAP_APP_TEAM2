package com.example.wapapp2.view.myprofile

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.wapapp2.R
import com.example.wapapp2.databinding.DialogEditMyprofileBinding
import com.example.wapapp2.observer.MyLifeCycleObserver
import com.example.wapapp2.viewmodel.MyAccountViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.lang.reflect.Type


class DialogEditDetailFragment : DialogFragment() {
    private var _binding: DialogEditMyprofileBinding? = null
    private val binding get() = _binding!!
    private val myAccountViewModel by activityViewModels<MyAccountViewModel>()

    private var myLifeCycleObserver: MyLifeCycleObserver? = null

    private var currentUri : Uri? = null

    companion object {
        const val TAG = "DialogEditDetailFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myLifeCycleObserver = MyLifeCycleObserver(requireActivity().activityResultRegistry, requireContext().applicationContext)
        lifecycle.addObserver(myLifeCycleObserver!!)

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): android.view.View? {
        _binding = DialogEditMyprofileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myAccountViewModel.myProfileData.observe(viewLifecycleOwner) { myUserDTO->
            binding.username.text = myUserDTO.name.toEditable()
            if(myUserDTO.imgUri.isEmpty().not())
                Glide.with(binding.root).load(myUserDTO.imgUri).circleCrop().into(binding.myProfileImg)
            else if (myUserDTO.gender == "man")
                binding.myProfileImg.setImageDrawable(ContextCompat.getDrawable(requireContext() ,R.drawable.man))
            else
                binding.myProfileImg.setImageDrawable(ContextCompat.getDrawable(requireContext() ,R.drawable.girl))

            binding.buttonDone.setOnClickListener {
                if (currentUri != null)
                    myAccountViewModel.SetMyProfileUri(currentUri!!)
                else
                    myAccountViewModel.DeleteMyProfileUrl()
                dismiss()
            }


            binding.buttonPic.setOnClickListener {
                val builder: MaterialAlertDialogBuilder =
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("프로필 사진 설정")
                        .setPositiveButton(
                            "사진 변경",
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.dismiss()
                                ShowCameraDialog()
                            })
                if (myUserDTO.imgUri.isEmpty().not() || currentUri != null)
                    builder.setNegativeButton("기본 이미지로 변경", DialogInterface.OnClickListener { dialog, which ->
                            if (myUserDTO.gender == "man")
                                binding.myProfileImg.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.man
                                    )
                                )
                            else
                                binding.myProfileImg.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.girl
                                    )
                                )
                            currentUri = null
                            dialog.dismiss()
                        })
                builder.show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun String.toEditable(): Editable = Editable.Factory().newEditable(this)



    private fun ShowCameraDialog(){
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(R.string.profile_modify)
            .setNegativeButton(R.string.close) { dialog, which ->
                dialog.dismiss()
            }
            .setNegativeButton(R.string.shoot_camera) { dialog, which ->
                dialog.dismiss()
                myLifeCycleObserver?.camera(requireActivity()) {
                    Glide.with(binding.root).load(it).circleCrop().into(binding.myProfileImg)
                    currentUri = it
                    //it?.apply {myAccountViewModel.SetMyProfileUri(this)}
                }
            }
            .setPositiveButton(R.string.pick_image) { dialog, which ->
                dialog.dismiss()

                myLifeCycleObserver?.pickImage(requireActivity()) {
                    Glide.with(binding.root).load(it).circleCrop().into(binding.myProfileImg)
                    currentUri = it
                    //it?.apply { myAccountViewModel.SetMyProfileUri(this) }
                }

            }.create().show()
    }



}