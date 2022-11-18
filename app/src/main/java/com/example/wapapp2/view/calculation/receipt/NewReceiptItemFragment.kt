package com.example.wapapp2.view.calculation.receipt

import android.os.Build.VERSION_CODES.N
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
import com.example.wapapp2.commons.classes.DelayTextWatcher
import com.example.wapapp2.databinding.NewReceiptItemViewFragmentBinding
import com.example.wapapp2.databinding.ProductItemLayoutInNewCalcBinding
import com.example.wapapp2.observer.MyLifeCycleObserver
import com.example.wapapp2.view.editreceipt.ReceiptImgFragment
import com.example.wapapp2.view.main.MainHostFragment
import com.example.wapapp2.viewmodel.NewReceiptViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class NewReceiptItemFragment : Fragment(), NewReceiptFragment.ReceiptDataGetter {
    private var _binding: NewReceiptItemViewFragmentBinding? = null
    private val binding get() = _binding!!

    private val newReceiptViewModel: NewReceiptViewModel by viewModels({ requireParentFragment() })
    private var receiptId: String? = null
    private var bundle: Bundle? = null
    private var myLifeCycleObserver: MyLifeCycleObserver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myLifeCycleObserver = MyLifeCycleObserver(requireActivity().activityResultRegistry, requireContext().applicationContext)
        lifecycle.addObserver(myLifeCycleObserver!!)

        bundle = arguments ?: savedInstanceState
        receiptId = bundle!!.getString("receiptId")

        newReceiptViewModel.addReceipt(receiptId!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = NewReceiptItemViewFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.titleText.addTextChangedListener(object : DelayTextWatcher() {
            override fun onFinalText(text: String) {
                if (text.isNotEmpty()) {
                    newReceiptViewModel.getReceiptDTO(receiptId!!)?.name = text
                } else {
                    newReceiptViewModel.getReceiptDTO(receiptId!!)?.name = "영수증"
                }
            }
        })

        binding.totalMoneyEditText.addTextChangedListener(object : DelayTextWatcher() {
            override fun onFinalText(text: String) {
                //총 금액 값이 0이상인 경우
                if (text.isNotEmpty()) {
                    if (newReceiptViewModel.getProductCount(receiptId!!) > 0) {
                        //영수증 항목이 존재하는 경우에 상단 총 금액 값과 영수증 항목 총 금액이 다를 경우
                        //상단 총 금액 항목의 값을 영수증 총 금액 항목으로 설정한다.
                        if (newReceiptViewModel.getReceiptDTO(receiptId!!)?.totalMoney != text.toInt()) {
                            binding.totalMoneyEditText.text =
                                    newReceiptViewModel.getReceiptDTO(receiptId!!)?.totalMoney.toString().toEditable()
                        }
                    } else {
                        newReceiptViewModel.getReceiptDTO(receiptId!!)?.totalMoney = text.toInt()
                    }
                } else {
                    //총 금액 값이 없는 경우
                    if (newReceiptViewModel.getProductCount(receiptId!!) > 0) {
                        val realTotalPrice = newReceiptViewModel.calcTotalPrice(receiptId!!)

                        //영수증 항목이 존재하는 경우에
                        //상단 총 금액 항목의 값을 영수증 총 금액 항목으로 설정한다.
                        binding.totalMoneyEditText.text = realTotalPrice.toEditable()
                    } else {
                        //영수증항목이 없을 때 0으로 설정
                        binding.totalMoneyEditText.text = "0".toEditable()
                        newReceiptViewModel.getReceiptDTO(receiptId!!)?.totalMoney = 0
                    }
                }


            }
        })


        binding.addProductBtn.setOnClickListener {
            addProduct()
        }

        binding.removeBtn.setOnClickListener {
            if (newReceiptViewModel.removeReceipt(receiptId!!)) {
                Toast.makeText(requireActivity(), R.string.removed_receipt, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireActivity(), R.string.unable_to_remove_receipt, Toast.LENGTH_SHORT).show()
            }
        }

        binding.receiptImage.setOnClickListener {
            newReceiptViewModel
                    .getReceiptDTO(receiptId!!)?.imgUriInMyPhone?.apply {
                        val receiptImgFragment = ReceiptImgFragment.newInstance(this.toString(),
                                ReceiptImgFragment.ImgType.LOCAL)

                        val fragmentManager = requireParentFragment().parentFragmentManager
                        fragmentManager.beginTransaction()
                                .hide(fragmentManager.findFragmentByTag(NewReceiptFragment.TAG) as Fragment)
                                .add(R.id.fragment_container_view, receiptImgFragment, ReceiptImgFragment.TAG)
                                .addToBackStack(ReceiptImgFragment.TAG).commit()
                    }

        }

        binding.receiptImgBtn.setOnClickListener {
            if (newReceiptViewModel.getReceiptDTO(receiptId!!)?.imgUriInMyPhone == null) {
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
                                    newReceiptViewModel.getReceiptDTO(receiptId!!)?.imgUriInMyPhone = this
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
                            newReceiptViewModel.getReceiptDTO(receiptId!!)?.imgUriInMyPhone = null
                            binding.receiptImgBtn.text = getString(R.string.add_img)
                        }
                        .setPositiveButton(R.string.replace_img) { dialog, which ->
                            dialog.dismiss()
                            pickImage()
                        }.create().show()
            }
        }
    }

    private fun pickImage() {
        myLifeCycleObserver?.pickImage(requireActivity()) {
            it?.apply {
                newReceiptViewModel.getReceiptDTO(receiptId!!)?.imgUriInMyPhone = this
                Glide.with(requireContext()).load(this).into(binding.receiptImage)
                binding.receiptImgBtn.text = getString(R.string.modify_img)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putAll(bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        myLifeCycleObserver?.apply { lifecycle.removeObserver(this) }
    }

    private fun addProduct() {
        val itemBinding = ProductItemLayoutInNewCalcBinding.inflate(layoutInflater)
        val productDTO = newReceiptViewModel.addProduct(receiptId!!)
        itemBinding.root.tag = productDTO
        val addedPosition = binding.productsList.childCount - 1

        itemBinding.priceEditText.addTextChangedListener(object : DelayTextWatcher() {
            override fun onFinalText(text: String) {
                if (text.isNotEmpty()) {
                    productDTO.price = text.toInt()
                } else {
                    itemBinding.priceEditText.text = "0".toEditable()
                    productDTO.price = 0
                }
                calcTotalPrice()
            }
        })

        itemBinding.nameEditText.addTextChangedListener(object : DelayTextWatcher() {
            override fun onFinalText(text: String) {
                if (text.isNotEmpty()) {
                    productDTO.name = text
                } else {
                    itemBinding.nameEditText.text = "".toEditable()
                    productDTO.name = ""
                }
            }
        })

        itemBinding.countEditText.addTextChangedListener(object : DelayTextWatcher() {
            override fun onFinalText(text: String) {
                if (text.isNotEmpty()) {
                    productDTO.count = text.toInt()
                } else {
                    itemBinding.countEditText.text = "1".toEditable()
                    productDTO.count = 1
                }
                calcTotalPrice()
            }
        })

        itemBinding.removeBtn.setOnClickListener {
            try {
                val position = newReceiptViewModel.removeProduct(receiptId!!, productDTO)
                binding.productsList.removeViewAt(position)
                calcTotalPrice()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.productsList.addView(itemBinding.root, addedPosition)
    }


    private fun calcTotalPrice() {
        binding.totalMoneyEditText.text = this.newReceiptViewModel.calcTotalPrice(receiptId!!).toEditable()
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun getTotalMoney(): String {
        return binding.totalMoneyEditText.text?.toString() ?: "0"
    }

}