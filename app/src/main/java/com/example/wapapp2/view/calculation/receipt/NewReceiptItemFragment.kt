package com.example.wapapp2.view.calculation.receipt

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.wapapp2.R
import com.example.wapapp2.databinding.NewReceiptItemViewFragmentBinding
import com.example.wapapp2.databinding.ProductItemLayoutInNewCalcBinding
import com.example.wapapp2.model.ReceiptProductDTO
import com.example.wapapp2.viewmodel.NewReceiptViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class NewReceiptItemFragment : Fragment() {
    private lateinit var binding: NewReceiptItemViewFragmentBinding
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
        binding = NewReceiptItemViewFragmentBinding.inflate(inflater)

        binding.removeReceiptImgBtn.visibility = View.GONE
        binding.editReceiptImgBtn.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        binding.toggles.addOnButtonCheckedListener { group, checkedId, isChecked ->
            when (checkedId) {
                binding.divideN.id -> {

                }
                binding.manualInput.id -> {

                }
                else -> {

                }
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

        binding.divideN.isSelected = true

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putAll(bundle)
    }

    private fun addProduct() {
        val itemBinding = ProductItemLayoutInNewCalcBinding.inflate(layoutInflater)
        val dto = newReceiptViewModel.addProduct(receiptId!!)
        itemBinding.root.tag = dto

        val addedPosition = binding.productsList.childCount - 1

        itemBinding.productPriceEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    dto.price = s.toString().toInt()
                    calcTotalPrice()
                } else {
                    itemBinding.productPriceEditText.text = "0".toEditable()
                }

            }
        })

        itemBinding.removeBtn.setOnClickListener {

            try {
                val position = newReceiptViewModel.removeProduct(receiptId!!, dto)
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

}