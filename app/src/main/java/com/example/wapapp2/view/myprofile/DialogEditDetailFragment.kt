package com.example.wapapp2.view.myprofile

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.wapapp2.R
import com.example.wapapp2.databinding.DialogEditMyprofileBinding
import com.example.wapapp2.observer.MyLifeCycleObserver
import com.example.wapapp2.viewmodel.MyAccountViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
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

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): android.view.View? {
        _binding = DialogEditMyprofileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myUserDTO = myAccountViewModel.myProfileData.value!!

        binding.username.text = myUserDTO.name.toEditable()

        if(myUserDTO.imgUri.isEmpty().not()) {
            Glide.with(binding.root).load(myUserDTO.imgUri).circleCrop().into(binding.myProfileImg)
            currentUri= Uri.EMPTY // 이미지가 있음을 empty로 표시 ( 없으면 널)
        }else if (myUserDTO.gender == "man")
            binding.myProfileImg.setImageDrawable(ContextCompat.getDrawable(requireContext() ,R.drawable.man))
        else
            binding.myProfileImg.setImageDrawable(ContextCompat.getDrawable(requireContext() ,R.drawable.girl))

        binding.buttonDone.setOnClickListener {
            //정보가 변경됨
            if(
                myUserDTO.name.equals(binding.username.text.toString()).not() ||
                (currentUri != null && currentUri != Uri.EMPTY) ||
                ((currentUri == null) && (myUserDTO.imgUri.isEmpty().not()))  ||
                binding.password.text.isEmpty().not()
            ) {
                if (binding.username.text.toString().isEmpty()){
                    Toast.makeText(requireContext(),"이름은 빈 값이 될 수 없습니다.",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("변경된 정보로 수정하시겠습니까?")
                    .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                        CoroutineScope(Dispatchers.Default).launch {
                            val tasks : ArrayList<Deferred<Boolean>> = arrayListOf()
                            //프로필사진 변경
                            tasks.add(async {
                                    if (currentUri != null) myAccountViewModel.SetMyProfileUri(currentUri!!)
                                    else myAccountViewModel.DeleteMyProfileUrl() })
                            //이름 변경
                            if(myUserDTO.name.equals(binding.username.text).not())
                                tasks.add( async { myAccountViewModel.UpdateName(binding.username.text.toString()) } )
                            //비밀번호 변경
                            if(binding.password.text.isEmpty().not())
                                 tasks.add( async{myAccountViewModel.UpdatePassword(binding.password.text.toString())} )

                            withContext(Main) {
                                tasks.awaitAll()?.apply {
                                    if (false in this)
                                        Toast.makeText(requireContext(),"변경에 실패하였습니다. 네트워크 연결을 확인해주세요",Toast.LENGTH_SHORT).show()
                                    this@DialogEditDetailFragment.dismiss()
                                }
                            }
                        }
                    })
                    .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                    }).show()
            }
            // 정보가 변경되지 않음
            else
                this@DialogEditDetailFragment.dismiss()
        }


        binding.buttonPic.setOnClickListener {
            val builder: MaterialAlertDialogBuilder =
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("프로필 사진 설정")
                    .setPositiveButton("사진 변경") { dialog, _ ->
                        dialog.dismiss()
                        ShowCameraDialog()
                    }
            if (myUserDTO.imgUri.isEmpty().not() || currentUri != null)
                builder.setNegativeButton("기본 이미지로 변경") { dialog, _ ->
                    if (myUserDTO.gender == "man") binding.myProfileImg.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.man))
                    else binding.myProfileImg.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.girl))
                    currentUri = null
                    dialog.dismiss()
                }
            builder.show()
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
                }
            }
            .setPositiveButton(R.string.pick_image) { dialog, which ->
                dialog.dismiss()

                myLifeCycleObserver?.pickImage(requireActivity()) {
                    Glide.with(binding.root).load(it).circleCrop().into(binding.myProfileImg)
                    currentUri = it
                }

            }.create().show()
    }

}