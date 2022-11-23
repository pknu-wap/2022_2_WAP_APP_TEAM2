package com.example.wapapp2.view.editreceipt

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.wapapp2.R
import com.example.wapapp2.databinding.FragmentEditReceiptBinding
import com.example.wapapp2.observer.MyLifeCycleObserver
import com.example.wapapp2.view.editreceipt.EditReceiptFragment.OnUpdatedValueListener
import com.example.wapapp2.viewmodel.ModifyReceiptViewModel
import com.example.wapapp2.viewmodel.ReceiptViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.*


class EditReceiptFragment : Fragment() {
    companion object {
        const val TAG = "EditReceiptFragment"
    }

    private var _binding: FragmentEditReceiptBinding? = null
    private val binding get() = _binding!!
    private val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd E a hh:mm", Locale.getDefault())

    private val receiptViewModel by viewModels<ReceiptViewModel>()
    private val modifyReceiptViewModel by viewModels<ModifyReceiptViewModel>()

    private val onUpdatedValueListener = OnUpdatedValueListener {
        binding.totalMoney.text = modifyReceiptViewModel.calcTotalPrice()
    }

    private val adapter = EditReceiptAdapter(onUpdatedValueListener)
    private var myLifeCycleObserver: MyLifeCycleObserver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            modifyReceiptViewModel.currentRoomId = getString("roomId")
            modifyReceiptViewModel.originalReceiptDTO = getParcelable("receiptDTO")!!
            modifyReceiptViewModel.originalReceiptDTO.apply {
                //영수증 복사
                modifyReceiptViewModel.modifiedReceiptDTO = this
                        .copy(id = id, createdTime = createdTime, imgUrl = imgUrl, imgUriInMyPhone = imgUriInMyPhone,
                                name = name, payersId = payersId, status = status,
                                totalMoney = totalMoney, productList = arrayListOf(), myMoney = 0, date = date)
            }
        }

        myLifeCycleObserver = MyLifeCycleObserver(requireActivity().activityResultRegistry, requireContext().applicationContext)
        lifecycle.addObserver(myLifeCycleObserver!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentEditReceiptBinding.inflate(inflater, container, false)
        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.rvEditreceipt.setHasFixedSize(true)
        binding.rvEditreceipt.adapter = adapter

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        modifyReceiptViewModel.originalReceiptDTO.apply {
            binding.totalMoney.text = totalMoney.toString()
            binding.titleText.text = name.toEditable()
            binding.dateTime.text = simpleDateFormat.format(createdTime!!)
        }

        receiptViewModel.products.observe(viewLifecycleOwner) {
            adapter.items = it
            modifyReceiptViewModel.modifiedReceiptDTO!!.getProducts().addAll(it)
        }
        receiptViewModel.getProducts(modifyReceiptViewModel.originalReceiptDTO.id!!, modifyReceiptViewModel.currentRoomId!!)

        binding.receiptImgBtn.setOnClickListener {
            if (modifyReceiptViewModel.originalReceiptDTO.imgUriInMyPhone == null) {
                MaterialAlertDialogBuilder(requireActivity())
                        .setTitle(R.string.add_img)
                        .setNegativeButton(R.string.close) { dialog, which ->
                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.shoot_camera) { dialog, which ->
                            dialog.dismiss()
                            myLifeCycleObserver?.camera(requireActivity()) {
                                it?.apply {
                                    Glide.with(requireContext()).load(this).into(binding.receiptImage)
                                    modifyReceiptViewModel.originalReceiptDTO.imgUriInMyPhone = this
                                    modifyReceiptViewModel.receiptImgChanged = true
                                    modifyReceiptViewModel.hasReceiptImg = true
                                }
                            }
                        }
                        .setPositiveButton(R.string.pick_image) { dialog, which ->
                            dialog.dismiss()
                            pickImage()
                        }.create().show()

            } else {
                MaterialAlertDialogBuilder(requireActivity())
                        .setTitle(R.string.modify_img)
                        .setNegativeButton(R.string.close) { dialog, which ->
                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.delete_img) { dialog, which ->
                            dialog.dismiss()
                            Glide.with(requireContext()).clear(binding.receiptImage)
                            modifyReceiptViewModel.originalReceiptDTO.imgUriInMyPhone = null
                            binding.receiptImgBtn.text = getString(R.string.add_img)
                            modifyReceiptViewModel.receiptImgChanged = true
                            modifyReceiptViewModel.hasReceiptImg = false
                        }
                        .setPositiveButton(R.string.replace_img) { dialog, which ->
                            dialog.dismiss()
                            pickImage()
                        }.create().show()
            }
        }

        binding.receiptImage.setOnClickListener {
            val imgType = if (modifyReceiptViewModel.receiptImgChanged) ReceiptImgFragment.ImgType.LOCAL
            else ReceiptImgFragment.ImgType.SERVER
            val imgFragment = ReceiptImgFragment.newInstance(modifyReceiptViewModel.originalReceiptDTO.imgUrl!!, imgType)

            parentFragmentManager.beginTransaction().hide(this@EditReceiptFragment)
                    .add(R.id.fragment_container_view, imgFragment, ReceiptImgFragment.TAG).addToBackStack(ReceiptImgFragment.TAG)
                    .commit()
        }

        //영수증  수정 버튼 클릭
        binding.confirmEdit.setOnClickListener {
            //다이얼로그로 최종 확인
            MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(R.string.modify_receipt)
                    .setMessage(R.string.msg_modify_receipt)
                    .setNegativeButton(R.string.cancel) { dialog, which ->
                        dialog.dismiss()
                    }.setPositiveButton(R.string.modify) { dialog, which ->

                        dialog.dismiss()
                    }.create().show()
        }

        if (modifyReceiptViewModel.originalReceiptDTO.imgUrl!!.isNotEmpty()) {
            modifyReceiptViewModel.hasReceiptImg = true
            val storageReference = Firebase.storage.getReferenceFromUrl(modifyReceiptViewModel.originalReceiptDTO.imgUrl!!)
            Glide.with(requireContext()).load(storageReference).into(binding.receiptImage)

            binding.receiptImgBtn.text = getString(R.string.modify_img)
        }
    }


    private fun pickImage() {
        myLifeCycleObserver?.pickImage(requireActivity()) {
            it?.apply {
                modifyReceiptViewModel.hasReceiptImg = true
                modifyReceiptViewModel.originalReceiptDTO.imgUriInMyPhone = this
                Glide.with(requireContext()).load(this).into(binding.receiptImage)
                binding.receiptImgBtn.text = getString(R.string.modify_img)
                modifyReceiptViewModel.receiptImgChanged = true
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun interface OnUpdatedValueListener {
        fun onUpdated()
    }

    private fun String.toEditable(): Editable = Editable.Factory().newEditable(this)

}