package com.example.wapapp2.view.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wapapp2.databinding.FragmentChatBinding
import com.example.wapapp2.dummy.DummyData
import com.example.wapapp2.view.calculation.CalcMainFragment
import com.example.wapapp2.viewmodel.MyAccountViewModel

class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private lateinit var chatAdapter: ChatMsgListAdapter
    private lateinit var myAccountViewModel: MyAccountViewModel
    private lateinit var bundle: Bundle

    private lateinit var viewHeightCallback: CalcMainFragment.ViewHeightCallback

    fun setViewHeightCallback(callback: CalcMainFragment.ViewHeightCallback) {
        this.viewHeightCallback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myAccountViewModel = ViewModelProvider(requireActivity())[MyAccountViewModel::class.java]
        chatAdapter = ChatMsgListAdapter(requireContext(), myAccountViewModel.myAccountId)

        bundle = (arguments ?: savedInstanceState) as Bundle
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        binding.chatList.apply {
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatAdapter.chatList.addAll(DummyData.getChatList())

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putAll(bundle)
    }

    /** setListener For Input Box **/
    private fun setInputListener() {
        binding.sendBtn.setOnClickListener {
            if (binding.textInputEditText.text!!.isNotEmpty()) {
                // 전송
            } else {
                Toast.makeText(requireContext(), "메시지를 입력해주세요!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}