package com.example.wapapp2.view.bankaccount

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.commons.classes.DialogSize
import com.example.wapapp2.commons.classes.DialogSize.Companion.setDialogSize
import com.example.wapapp2.commons.interfaces.ListOnClickListener
import com.example.wapapp2.databinding.AccountDialogBinding
import com.example.wapapp2.databinding.ApplistItemBinding
import com.example.wapapp2.model.BankAccountDTO
import com.example.wapapp2.model.BankAppDTO
import com.example.wapapp2.viewmodel.BankTransferViewModel


class BankTransferDialogFragment : DialogFragment() {
    private var _binding: AccountDialogBinding? = null
    private val binding get() = _binding!!

    private val onClickedBankAppListener = ListOnClickListener<BankAppDTO> { dto, pos ->
        openApp(dto.packageName)
    }
    private val adapter: BanksAdapter = BanksAdapter(onClickedBankAppListener)
    private val bankTransferViewModel: BankTransferViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bankTransferViewModel.selectedBankAccount = arguments?.getParcelable("bankAccountDTO")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("bankAccountDTO", bankTransferViewModel.selectedBankAccount)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        _binding = AccountDialogBinding.inflate(inflater, container, false)
        return binding.root
    }


    companion object {
        const val TAG = "BankTransferDialogFragment"

        fun newInstance(bankAccountDTO: BankAccountDTO) =
                BankTransferDialogFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("bankAccountDTO", bankAccountDTO)
                    }
                }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bankList.adapter = adapter
        DialogSize.setDialogSize(requireDialog(), 90, 70)

        val clipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val data =
                "${bankTransferViewModel.selectedBankAccount?.accountNumber} ${bankTransferViewModel.selectedBankAccount?.bankDTO!!.bankName}"
        val clipData = ClipData.newPlainText("accountNumber", bankTransferViewModel.selectedBankAccount?.accountNumber)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(requireContext(), "$data 복사가 완료되었습니다!", Toast.LENGTH_SHORT).show()

        bankTransferViewModel.installedBankApps.observe(viewLifecycleOwner) {
            adapter.apply {
                setApps(it)
            }
        }

        binding.bankName.text = bankTransferViewModel.selectedBankAccount!!.bankDTO!!.bankName
        binding.icon.setImageResource(bankTransferViewModel.selectedBankAccount!!.bankDTO!!.iconId)
        val account =
                "${bankTransferViewModel.selectedBankAccount!!.accountNumber} ${bankTransferViewModel.selectedBankAccount!!.accountHolder}"
        binding.bankAccountNumber.text = account
        bankTransferViewModel.loadInstalledBankApps(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openApp(packageName: String) {
        val intent: Intent? = requireContext().packageManager.getLaunchIntentForPackage(packageName)
        intent?.let {
            startActivity(it)
        }
    }

    private class BanksAdapter(val onClickedBankAppListener: ListOnClickListener<BankAppDTO>) :
            RecyclerView
            .Adapter<RecyclerView
            .ViewHolder>() {
        var bankApps: ArrayList<BankAppDTO> = ArrayList<BankAppDTO>()

        @SuppressLint("NotifyDataSetChanged")
        fun setApps(apps: ArrayList<BankAppDTO>) {
            bankApps.clear()
            bankApps.addAll(apps)
            notifyDataSetChanged()
        }

        private class BankVH(val binding: ApplistItemBinding, val onClickedBankAppListener: ListOnClickListener<BankAppDTO>) :
                RecyclerView.ViewHolder(binding.root) {
            fun bind(bankAppDTO: BankAppDTO) {
                binding.appName.text = bankAppDTO.appName
                binding.appIcon.setImageDrawable(bankAppDTO.appIcon)

                binding.root.setOnClickListener {
                    onClickedBankAppListener.onClicked(bankAppDTO, adapterPosition)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return BankVH(ApplistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), onClickedBankAppListener)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as BankVH).bind(bankApps[position])
        }

        override fun getItemCount(): Int = bankApps.size


    }
}