package com.example.wapapp2.view.calculation.receipt

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.wapapp2.R
import com.example.wapapp2.commons.classes.DelayTextWatcher
import com.example.wapapp2.databinding.NewReceiptItemViewFragmentBinding
import com.example.wapapp2.databinding.ProductItemLayoutInNewCalcBinding
import com.example.wapapp2.viewmodel.NewReceiptViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class NewReceiptItemFragment : Fragment(), NewReceiptFragment.ReceiptDataGetter {
    private var _binding: NewReceiptItemViewFragmentBinding? = null
    private val binding get() = _binding!!

    private val newReceiptViewModel: NewReceiptViewModel by viewModels({ requireParentFragment() })
    private var receiptId: String? = null
    private var bundle: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bundle = arguments ?: savedInstanceState
        receiptId = bundle!!.getString("receiptId")

        newReceiptViewModel.addReceipt(receiptId!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = NewReceiptItemViewFragmentBinding.inflate(inflater)

        binding.removeReceiptImgBtn.visibility = View.GONE
        binding.editReceiptImgBtn.visibility = View.GONE

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


        binding.addReceiptImgBtn.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(R.string.add_receipt)
                    .setNegativeButton(R.string.exit) { dialog, which ->
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.shoot_camera) { dialog, which ->
                        dialog.dismiss()

                    }
                    .setPositiveButton(R.string.pick_image) { dialog, which ->
                        dialog.dismiss()
                        //myObserver.pickImage()
                    }.create()

            dialog.show()
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

    private fun addProduct() {
        val itemBinding = ProductItemLayoutInNewCalcBinding.inflate(layoutInflater)
        val productDTO = newReceiptViewModel.addProduct(receiptId!!)
        itemBinding.root.tag = productDTO

        val addedPosition = binding.productsList.childCount - 1

        itemBinding.productPriceEditText.addTextChangedListener(object : DelayTextWatcher() {
            override fun onFinalText(text: String) {
                if (text.isNotEmpty()) {
                    productDTO.price = text.toInt()
                } else {
                    itemBinding.productPriceEditText.text = "0".toEditable()
                    productDTO.price = 0
                }
                calcTotalPrice()
            }
        })

        itemBinding.calculationItemNameEditText.addTextChangedListener(object : DelayTextWatcher() {
            override fun onFinalText(text: String) {
                if (text.isNotEmpty()) {
                    productDTO.name = text
                } else {
                    itemBinding.calculationItemNameEditText.text = "".toEditable()
                    productDTO.name = ""
                }
            }
        })

        itemBinding.removeBtn.setOnClickListener {
            try {
                val position = newReceiptViewModel.removeProduct(receiptId!!, productDTO)
                binding.productsList.removeViewAt(position)
                calcTotalPrice()
            } catch (e: Exception) {

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