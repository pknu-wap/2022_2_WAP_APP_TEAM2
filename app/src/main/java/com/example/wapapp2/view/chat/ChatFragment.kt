package com.example.wapapp2.view.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wapapp2.databinding.FragmentChatBinding
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.view.calculation.CalcMainFragment
import com.example.wapapp2.viewmodel.ChatViewModel
import com.example.wapapp2.viewmodel.MyAccountViewModel
import java.util.*


class ChatFragment(val calcRoomDTO: CalcRoomDTO) : Fragment(), ScrollListener {

    private lateinit var binding: FragmentChatBinding
    private lateinit var chatAdapter_: ChatAdapter
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
        chatAdapter_ = ChatAdapter(myAccountViewModel.myProfileData.value!!.id, chatViewModel.getOptions(calcRoomDTO),
                this@ChatFragment::ScrollToBottom)
        bundle = (arguments ?: savedInstanceState) as Bundle
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        binding.chatList.apply {
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            this.adapter = chatAdapter_
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
                binding.chatList.smoothScrollToPosition(chatAdapter_.snapshots.size)
                // 전송
            } else {
                Toast.makeText(requireContext(), "메시지를 입력해주세요!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        chatViewModel.attach(calcRoomDTO)
        chatAdapter_.startListening()
        super.onStart()
    }

    override fun onStop() {
        chatAdapter_.stopListening()
        super.onStop()
    }

    override fun ScrollToBottom() {
        binding.chatList.smoothScrollToPosition(chatAdapter_.snapshots.size)
    }

}