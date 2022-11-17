package com.example.wapapp2.view.editreceipt

import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class EditReceiptFragment : Fragment() {
    companion object {
        const val TAG = "EditReceiptFragment"
    }

    private var _binding: FragmentEditReceiptBinding? = null
    private val binding get() = _binding!!
    private val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd E a hh:mm", Locale.getDefault())

    private var calcRoomId: String? = null

    private val receiptViewModel by viewModels<ReceiptViewModel>()
    private val modifyReceiptViewModel by viewModels<ModifyReceiptViewModel>()

    private val onUpdatedValueListener = OnUpdatedValueListener {
        val newTotalMoney = receiptViewModel.calcTotalPrice()
        modifyReceiptViewModel.receiptDTO.totalMoney = newTotalMoney.toInt()
        binding.totalMoney.text = modifyReceiptViewModel.receiptDTO.totalMoney.toString()
    }

    private val adapter = EditReceiptAdapter(onUpdatedValueListener = onUpdatedValueListener)
    private var myLifeCycleObserver: MyLifeCycleObserver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            calcRoomId = getString("roomId")
            modifyReceiptViewModel.receiptDTO = getParcelable("receiptDTO")!!
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

        modifyReceiptViewModel.receiptDTO.apply {
            binding.totalMoney.text = totalMoney.toString().toEditable()
            binding.titleText.text = name.toEditable()
            binding.dateTime.text = simpleDateFormat.format(createdTime!!)
        }

        receiptViewModel.products.observe(viewLifecycleOwner) {
            adapter.items = it
        }
        receiptViewModel.getProducts(modifyReceiptViewModel.receiptDTO.id!!, calcRoomId!!)

        binding.receiptImgBtn.setOnClickListener {
            if (modifyReceiptViewModel.receiptDTO.imgUriInMyPhone == null) {
                MaterialAlertDialogBuilder(requireActivity())
                        .setTitle(R.string.add_img)
                        .setNegativeButton(R.string.exit) { dialog, which ->
                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.shoot_camera) { dialog, which ->
                            dialog.dismiss()
                            myLifeCycleObserver?.camera(requireActivity()) {
                                it?.apply {
                                    Glide.with(requireContext()).load(this).into(binding.receiptImage)
                                    modifyReceiptViewModel.receiptDTO.imgUriInMyPhone = this
                                    modifyReceiptViewModel.receiptImgChanged = true
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
                        .setNegativeButton(R.string.exit) { dialog, which ->
                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.delete_img) { dialog, which ->
                            dialog.dismiss()
                            Glide.with(requireContext()).clear(binding.receiptImage)
                            modifyReceiptViewModel.receiptDTO.imgUriInMyPhone = null
                            binding.receiptImgBtn.text = getString(R.string.add_img)
                            modifyReceiptViewModel.receiptImgChanged = true
                        }
                        .setPositiveButton(R.string.replace_img) { dialog, which ->
                            dialog.dismiss()
                            pickImage()
                        }.create().show()
            }
        }

        modifyReceiptViewModel.receiptDTO.imgUrl?.apply {
            val storageReference = Firebase.storage.getReferenceFromUrl(this)
            Glide.with(requireContext()).load(storageReference).into(binding.receiptImage)

            binding.receiptImage.setOnClickListener {
                val imgType = if (modifyReceiptViewModel.receiptImgChanged) ReceiptImgFragment.ImgType.LOCAL
                else ReceiptImgFragment.ImgType.SERVER
                val imgFragment = ReceiptImgFragment.newInstance(modifyReceiptViewModel.receiptDTO.imgUrl!!, imgType)

                parentFragmentManager.beginTransaction().hide(this@EditReceiptFragment)
                        .add(R.id.fragment_container_view, imgFragment, ReceiptImgFragment.TAG).addToBackStack(ReceiptImgFragment.TAG)
                        .commit()
            }
        }
    }


    private fun pickImage() {
        myLifeCycleObserver?.pickImage(requireActivity()) {
            modifyReceiptViewModel.receiptDTO.imgUriInMyPhone = it
            Glide.with(requireContext()).load(it).into(binding.receiptImage)
            binding.receiptImgBtn.text = getString(R.string.modify_img)
            modifyReceiptViewModel.receiptImgChanged = true
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