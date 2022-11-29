package com.example.wapapp2.view.checkreceipt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wapapp2.R
import com.example.wapapp2.commons.classes.ListAdapterDataObserver
import com.example.wapapp2.commons.classes.WrapContentLinearLayoutManager
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.commons.interfaces.ListOnLongClickListener
import com.example.wapapp2.databinding.FragmentCheckReceiptBinding
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.view.editreceipt.EditReceiptFragment
import com.example.wapapp2.view.receipt.ReceiptInfoFragment
import com.example.wapapp2.viewmodel.ModifyReceiptViewModel
import com.example.wapapp2.viewmodel.MyAccountViewModel
import com.example.wapapp2.viewmodel.ReceiptViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ReceiptsFragment : Fragment() {
    private var _binding: FragmentCheckReceiptBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val TAG = "ReceiptsFragment"
    }

    private lateinit var adapter: ReceiptsAdapter
    private var roomId: String? = null
    private val receiptViewModel by viewModels<ReceiptViewModel>()
    private val modifyReceiptViewModel by viewModels<ModifyReceiptViewModel>()
    private var dataObserver: ListAdapterDataObserver? = null

    private val receiptOnClickListener = ListOnClickListener<ReceiptDTO> { item, _position ->
        // 영수증 정보 화면 표시
        val receiptInfoFragment = ReceiptInfoFragment.newInstance(receiptViewModel.currentRoomId!!, item)

        parentFragmentManager.beginTransaction().hide(this@ReceiptsFragment)
                .add(R.id.fragment_container_view, receiptInfoFragment, ReceiptInfoFragment.TAG).addToBackStack(ReceiptInfoFragment.TAG)
                .commit()
    }


    private val receiptOnLongClickListener = ListOnLongClickListener<ReceiptDTO> { item, pos ->
        //정산 상태 확인
        //정산 완료되었으면 영수증 삭제만 표시
        val dialogBuilder = MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.work_for_receipt)
                .setCancelable(false).setNegativeButton(R.string.close) { dialog, which ->
                    dialog.dismiss()
                }

        //정산 완료
        if (item.status) {
            dialogBuilder.setMessage(R.string.available_remove_and_modify)
                    .setNeutralButton(R.string.remove) { dialog, which ->
                        dialog.dismiss()
                        showRemoveReceiptDialog(item)
                    }
        } else {
            dialogBuilder.setMessage(R.string.available_remove_and_modify)
                    .setNeutralButton(R.string.remove) { dialog, which ->
                        dialog.dismiss()
                        showRemoveReceiptDialog(item)
                    }.setPositiveButton(R.string.modify) { dialog, which ->
                        dialog.dismiss()

                        //영수증 수정 화면으로 이동
                        val fragment = EditReceiptFragment()
                        fragment.arguments = Bundle().apply {
                            putParcelable("receiptDTO", item)
                            putString("roomId", roomId)
                        }

                        parentFragmentManager
                                .beginTransaction()
                                .hide(this@ReceiptsFragment)
                                .add(R.id.fragment_container_view, fragment, EditReceiptFragment.TAG)
                                .addToBackStack(EditReceiptFragment.TAG)
                                .commit()
                    }
        }
        dialogBuilder.create().show()
    }

    private fun showRemoveReceiptDialog(item: ReceiptDTO) {
        MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.remove_receipt)
                .setCancelable(false).setNegativeButton(R.string.close) { dialog, which ->
                    dialog.dismiss()
                }.setPositiveButton(R.string.remove) { dialog, which ->
                    dialog.dismiss()
                    modifyReceiptViewModel.removeReceipt(receiptViewModel.currentRoomId!!, item)
                }
                .create().show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            roomId = getString("roomId")
            receiptViewModel.currentRoomId = roomId
        }
        adapter =
                ReceiptsAdapter(receiptOnClickListener, receiptOnLongClickListener, receiptViewModel.getReceiptsRecyclerOptions(receiptViewModel
                        .currentRoomId!!))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCheckReceiptBinding.inflate(inflater, container, false)
        binding.loadingView.setContentView(binding.rvEditreceipt)

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        dataObserver = ListAdapterDataObserver(binding.rvEditreceipt, binding.rvEditreceipt.layoutManager as
                WrapContentLinearLayoutManager, adapter)
        dataObserver!!.registerLoadingView(binding.loadingView, getString(R.string.empty_receipts))
        dataObserver!!.onChanged()
        adapter.registerAdapterDataObserver(dataObserver!!)

        binding.rvEditreceipt.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        dataObserver?.run {
            adapter.unregisterAdapterDataObserver(this)
            null
        }
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            adapter.stopListening()
        } else {
            adapter.startListening()
        }
    }


}