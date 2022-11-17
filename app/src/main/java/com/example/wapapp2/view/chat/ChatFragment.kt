package com.example.wapapp2.view.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wapapp2.databinding.FragmentChatBinding
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.view.calculation.CalcMainFragment
import com.example.wapapp2.viewmodel.ChatViewModel
import com.example.wapapp2.viewmodel.MyAccountViewModel
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import java.util.*


class ChatFragment(val calcRoomDTO: CalcRoomDTO) : Fragment(), ScrollListener {

    private lateinit var binding: FragmentChatBinding

    private lateinit var chatAdapter: ChatPagingAdapter
    private lateinit var bundle: Bundle

    private lateinit var viewHeightCallback: CalcMainFragment.ViewHeightCallback
    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var myAccountViewModel: MyAccountViewModel


    fun setViewHeightCallback(callback: CalcMainFragment.ViewHeightCallback) {
        this.viewHeightCallback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myAccountViewModel = ViewModelProvider(requireActivity())[MyAccountViewModel::class.java]

        val options = FirestorePagingOptions.Builder<ChatDTO>()
            .setLifecycleOwner(this)
            .setQuery(chatViewModel.getQueryForOption(calcRoomDTO) { _, _ -> ScrollToBottom() },
                PagingConfig( 20,  10, false),
                ChatDTO::class.java)
            .build()
        chatAdapter = ChatPagingAdapter(myAccountViewModel.myProfileData.value!!.id, options,
                this@ChatFragment::ScrollToBottom)
        bundle = (arguments ?: savedInstanceState) as Bundle
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        binding.chatList.apply {
            val lm = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            lm.stackFromEnd = true
            layoutManager = lm
            this.adapter = chatAdapter
        }
        setInputListener()

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (binding.textInputLayout.height > 0) {
                    binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    viewHeightCallback.height(binding.textInputLayout.height)
                }
            }
        })
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putAll(bundle)
    }

    /** setListener For Input Box **/
    private fun setInputListener() {
        binding.sendBtn.setOnClickListener {
            if (binding.textInputEditText.text!!.isNotEmpty()) {

                val newChat = ChatDTO(myAccountViewModel.myProfileData.value!!.name, Date(), binding.textInputLayout.editText!!.text
                        .toString(), myAccountViewModel.myProfileData.value!!.id)

                binding.textInputLayout.editText!!.text.clear()
                chatViewModel.sendMsg(newChat)
                binding.chatList.smoothScrollToPosition(chatAdapter.snapshot().size)
                // 전송
            } else {
                Toast.makeText(requireContext(), "메시지를 입력해주세요!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        chatViewModel.attach(calcRoomDTO)
        chatAdapter.startListening()
        super.onStart()
    }

    override fun onStop() {
        chatAdapter.stopListening()
        super.onStop()
    }

    override fun ScrollToBottom() {
        binding.chatList.smoothScrollToPosition(chatAdapter.snapshot().size)
    }

}