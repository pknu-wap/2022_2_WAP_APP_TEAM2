package com.example.wapapp2.view.editreceipt

import android.graphics.Bitmap
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
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.observer.MyLifeCycleObserver
import com.example.wapapp2.viewmodel.ReceiptViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
    private var receiptDTO: ReceiptDTO? = null

    private val receiptViewModel by viewModels<ReceiptViewModel>()
    private val onUpdatedValueListener = OnUpdatedValueListener {
        val newTotalMoney = receiptViewModel.calcTotalPrice()
        receiptDTO?.totalMoney = newTotalMoney.toInt()
        binding.totalMoney.text = receiptDTO?.totalMoney.toString()
    }

    private val adapter = EditReceiptAdapter(onUpdatedValueListener = onUpdatedValueListener)
    private var myLifeCycleObserver: MyLifeCycleObserver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            calcRoomId = getString("calcRoomId")
            receiptDTO = getParcelable("receiptDTO")
        }
        myLifeCycleObserver = MyLifeCycleObserver(requireActivity().activityResultRegistry)
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

        receiptDTO?.apply {
            binding.totalMoney.text = totalMoney.toString().toEditable()
            binding.titleText.text = name.toEditable()
            binding.dateTime.text = simpleDateFormat.format(createdTime!!)

            imgUrl?.apply {
                //이미지 표시
            }
        }

        receiptViewModel.products.observe(viewLifecycleOwner) {
            adapter.items = it
        }
        receiptViewModel.getProducts(receiptDTO?.id!!, calcRoomId!!)

        binding.receiptImgBtn.setOnClickListener {
            if (receiptDTO?.imgUriInMyPhone == null) {
                MaterialAlertDialogBuilder(requireActivity())
                        .setTitle(R.string.add_img)
                        .setNegativeButton(R.string.exit) { dialog, which ->
                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.shoot_camera) { dialog, which ->
                            dialog.dismiss()
                            myLifeCycleObserver?.camera(requireActivity()) {
                                it.data?.extras?.apply {
                                    val bitmap = get("data") as Bitmap
                                    Glide.with(requireContext()).load(bitmap).into(binding.receiptImage)
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
                            receiptDTO?.imgUriInMyPhone = null
                            binding.receiptImgBtn.text = getString(R.string.add_img)
                        }
                        .setPositiveButton(R.string.receipt_img) { dialog, which ->
                            dialog.dismiss()
                            pickImage()
                        }.create().show()
            }
        }
    }


    private fun pickImage() {
        myLifeCycleObserver?.pickImage(requireActivity()) {
            receiptDTO?.imgUriInMyPhone = it
            Glide.with(requireContext()).load(it).into(binding.receiptImage)

            binding.receiptImgBtn.text = getString(R.string.modify_img)
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