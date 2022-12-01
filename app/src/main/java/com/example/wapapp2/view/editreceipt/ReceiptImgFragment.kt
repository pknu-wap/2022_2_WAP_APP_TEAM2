package com.example.wapapp2.view.editreceipt

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.example.wapapp2.R
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
        binding.loadingView.setContentView(getString(R.string.failed_loading_image), binding.receiptImg)
        binding.loadingView.onStarted()

        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        savedInstanceState?.apply {
            imgUrl = getString("imgUrl")
            imgType = getParcelable("imgType")
        }

        if (imgType == ImgType.SERVER) {
            val storageReference = Firebase.storage.getReferenceFromUrl(imgUrl!!)
            val target = object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Glide.with(requireContext().applicationContext).load(resource).into(binding.receiptImg)
                    binding.loadingView.onSuccessful()
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            }
            Glide.with(this).asBitmap().load(storageReference).into(target)
        } else {
            Glide.with(this).load(Uri.parse(imgUrl)).into(binding.receiptImg)
            binding.loadingView.onSuccessful()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("imgUrl", imgUrl)
        outState.putParcelable("imgType", imgType)
    }
}