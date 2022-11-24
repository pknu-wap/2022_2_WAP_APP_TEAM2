package com.example.wapapp2.view.receipt

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.wapapp2.R
import com.example.wapapp2.commons.classes.LoadingDialogView
import com.example.wapapp2.databinding.FragmentReceiptInfoBinding
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.view.editreceipt.ReceiptImgFragment
import com.example.wapapp2.viewmodel.ReceiptViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.*


class ReceiptInfoFragment : Fragment() {
    private var _binding: FragmentReceiptInfoBinding? = null
    private val binding get() = _binding!!
    private val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd E a hh:mm", Locale.getDefault())
    private val receiptViewModel by viewModels<ReceiptViewModel>()

    companion object {
        const val TAG = "ReceiptInfoFragment"

        fun newInstance(roomId: String, receiptDTO: ReceiptDTO): ReceiptInfoFragment {
            val fragment = ReceiptInfoFragment()
            fragment.arguments = Bundle().apply {
                putString("roomId", roomId)
                putParcelable("receiptDTO", receiptDTO)
            }

            return fragment
        }
    }


    private val adapter = ReceiptProductsAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        LoadingDialogView.showDialog(requireActivity(), getString(R.string.loading_receipt_info))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.apply {
            receiptViewModel.currentReceiptDTO = getParcelable("receiptDTO")!!
            receiptViewModel.currentRoomId = getString("roomId")
            receiptViewModel.currentReceiptId = receiptViewModel.currentReceiptDTO!!.id

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentReceiptInfoBinding.inflate(inflater, container, false)

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.receiptList.adapter = adapter

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        receiptViewModel.currentReceiptDTO!!.apply {
            val totalMoneyTxt = "${totalMoney.toString()}원"
            binding.totalMoney.text = totalMoneyTxt
            binding.title.text = name
            binding.createdDateTime.text = simpleDateFormat.format(createdTime!!)

            if (imgUrl.isNullOrEmpty())
                binding.receiptImage.visibility = View.GONE
            else {
                val storageReference = Firebase.storage.getReferenceFromUrl(imgUrl!!)
                Glide.with(requireContext()).load(storageReference).into(binding.receiptImage)

                binding.receiptImage.setOnClickListener {
                    val imgFragment = ReceiptImgFragment.newInstance(imgUrl!!, ReceiptImgFragment.ImgType.LOCAL)

                    parentFragmentManager.beginTransaction().hide(this@ReceiptInfoFragment)
                            .add(R.id.fragment_container_view, imgFragment, ReceiptImgFragment.TAG)
                            .addToBackStack(ReceiptImgFragment.TAG).commit()
                }

            }
        }

        receiptViewModel.products.observe(viewLifecycleOwner) {
            adapter.modelList.addAll(it)
            adapter.notifyDataSetChanged()
            LoadingDialogView.clearDialogs()
        }
        receiptViewModel.getProducts(receiptViewModel.currentReceiptId!!, receiptViewModel.currentRoomId!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}