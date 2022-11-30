package com.example.wapapp2.view.editreceipt

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.wapapp2.R
import com.example.wapapp2.commons.classes.DataTypeConverter
import com.example.wapapp2.commons.classes.DelayTextWatcher
import com.example.wapapp2.databinding.FragmentEditReceiptBinding
import com.example.wapapp2.observer.MyLifeCycleObserver
import com.example.wapapp2.view.calculation.CalcMainFragment
import com.example.wapapp2.view.editreceipt.EditReceiptFragment.OnUpdatedValueListener
import com.example.wapapp2.viewmodel.CurrentCalcRoomViewModel
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
    private val calcRoomViewModel by viewModels<CurrentCalcRoomViewModel>({
        parentFragmentManager.findFragmentByTag(CalcMainFragment.TAG)!!
    })

    private val onUpdatedValueListener = OnUpdatedValueListener {
        binding.totalMoney.text = DataTypeConverter.toKRW(modifyReceiptViewModel.calcTotalPrice().toInt())
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
                        .copy(id = id, createdTime = createdTime, imgUrl = imgUrl,
                                name = name, payersId = payersId, status = status,
                                totalMoney = totalMoney, productList = arrayListOf(), date = date, payersName = payersName)
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
            binding.totalMoney.text = DataTypeConverter.toKRW(totalMoney)
            binding.titleText.text = name.toEditable()
            binding.dateTime.text = simpleDateFormat.format(createdTime!!)
        }

        receiptViewModel.products.observe(viewLifecycleOwner) {
            modifyReceiptViewModel.setOriginalProducts(it)
            adapter.items = modifyReceiptViewModel.modifiedProductMap.values.toMutableList()
        }
        receiptViewModel.getProducts(modifyReceiptViewModel.originalReceiptDTO.id!!, modifyReceiptViewModel.currentRoomId!!)

        binding.receiptImgBtn.setOnClickListener {
            if (modifyReceiptViewModel.hasReceiptImg)
                dialogModifyImg()
            else
                dialogAddImg()
        }

        binding.receiptImage.setOnClickListener {
            val imgType = if (modifyReceiptViewModel.receiptImgChanged) ReceiptImgFragment.ImgType.LOCAL
            else ReceiptImgFragment.ImgType.SERVER
            val imgFragment = ReceiptImgFragment.newInstance(modifyReceiptViewModel.modifiedReceiptDTO.imgUrl!!, imgType)

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
                        modifyReceiptViewModel.modifyReceipt(modifyReceiptViewModel.currentRoomId!!)
                        modifyReceiptViewModel.modifyProducts(modifyReceiptViewModel.currentRoomId!!)

                        dialog.dismiss()
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }.create().show()
        }


        binding.titleText.addTextChangedListener(object : DelayTextWatcher() {
            override fun onFinalText(text: String) {
                modifyReceiptViewModel.modifiedReceiptDTO.name = text
            }
        })

        if (modifyReceiptViewModel.originalReceiptDTO.imgUrl.isNotEmpty()) {
            modifyReceiptViewModel.hasReceiptImg = true
            val storageReference = Firebase.storage.getReferenceFromUrl(modifyReceiptViewModel.originalReceiptDTO.imgUrl!!)
            Glide.with(requireContext()).load(storageReference).into(binding.receiptImage)

            binding.receiptImgBtn.text = getString(R.string.modify_img)
        }
    }

    private fun dialogModifyImg() {
        MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.modify_img)
                .setNegativeButton(R.string.close) { dialog, which ->
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.delete_img) { dialog, which ->
                    dialog.dismiss()
                    Glide.with(requireContext()).clear(binding.receiptImage)

                    modifyReceiptViewModel.modifiedReceiptDTO.imgUriInMyPhone = null
                    binding.receiptImgBtn.text = getString(R.string.add_img)
                    modifyReceiptViewModel.receiptImgChanged = true
                    modifyReceiptViewModel.hasReceiptImg = false
                }
                .setPositiveButton(R.string.replace_img) { dialog, which ->
                    dialog.dismiss()
                    pickImage()
                }.create().show()
    }

    private fun dialogAddImg() {
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
                            modifyReceiptViewModel.modifiedReceiptDTO.imgUriInMyPhone = this
                            modifyReceiptViewModel.receiptImgChanged = true
                            modifyReceiptViewModel.hasReceiptImg = true
                        }
                    }
                }
                .setPositiveButton(R.string.pick_image) { dialog, which ->
                    dialog.dismiss()
                    pickImage()
                }.create().show()

    }

    private fun pickImage() {
        myLifeCycleObserver?.pickImage(requireActivity()) {
            it?.apply {
                modifyReceiptViewModel.hasReceiptImg = true
                modifyReceiptViewModel.modifiedReceiptDTO.imgUriInMyPhone = this
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