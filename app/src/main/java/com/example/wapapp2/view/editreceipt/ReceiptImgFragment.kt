package com.example.wapapp2.view.editreceipt

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.wapapp2.databinding.FragmentReceiptImgBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.parcelize.Parcelize


class ReceiptImgFragment : Fragment() {
    private var _binding: FragmentReceiptImgBinding? = null
    private val binding get() = _binding!!
    private var imgUrl: String? = null
    private var imgType: ImgType? = null


    @Parcelize
    enum class ImgType : Parcelable {
        LOCAL, SERVER
    }

    companion object {
        const val TAG = "ReceiptImgFragment"

        fun newInstance(imgUrl: String, imgType: ImgType): ReceiptImgFragment {
            val fragment = ReceiptImgFragment()
            fragment.arguments = Bundle().apply {
                putString("imgUrl", imgUrl)
                putParcelable("imgType", imgType)
            }
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            imgUrl = getString("imgUrl")
            imgType = getParcelable("imgType")
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentReceiptImgBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        savedInstanceState?.apply {
            imgUrl = getString("imgUrl")
            imgType = getParcelable("imgType")
        }

        if (imgType == ImgType.SERVER) {
            val storageReference = Firebase.storage.reference.getFile(Uri.parse(imgUrl!!))
            Glide.with(this).load(storageReference).into(binding.receiptImg)
        } else {
            Glide.with(this).load(Uri.parse(imgUrl)).into(binding.receiptImg)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("imgUrl", imgUrl)
        outState.putParcelable("imgType", imgType)
    }
}