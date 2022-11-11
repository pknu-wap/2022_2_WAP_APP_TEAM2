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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wapapp2.databinding.FragmentChatBinding
import com.example.wapapp2.model.CalcRoomDTO
import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.view.calculation.CalcMainFragment
import com.example.wapapp2.viewmodel.ChatViewModel
import com.example.wapapp2.viewmodel.MyAccountViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class ChatFragment(val calcRoomDTO : CalcRoomDTO) : Fragment() {
    private lateinit var binding: FragmentChatBinding
    //private lateinit var chatAdapter: ChatMsgListAdapter
    private lateinit var chatAdapter_: ChatAdapter
    private lateinit var myAccountViewModel: MyAccountViewModel
    private lateinit var bundle: Bundle

    private lateinit var viewHeightCallback: CalcMainFragment.ViewHeightCallback
    private val chatViewModel : ChatViewModel by viewModels()


    fun setViewHeightCallback(callback: CalcMainFragment.ViewHeightCallback) {
        this.viewHeightCallback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myAccountViewModel = ViewModelProvider(requireActivity())[MyAccountViewModel::class.java]

        //수정필요 -> 생명주기 공부
        chatViewModel.attach(calcRoomDTO)
        CoroutineScope(Dispatchers.Default).launch{
            async {  chatAdapter_ = ChatAdapter("PqbxywH1wjMHbkcWBfkD9annA5Z2", chatViewModel.getOptions())
            }
        }

        //chatAdapter = ChatMsgListAdapter(requireContext(), myAccountViewModel.myAccountId)

        bundle = (arguments ?: savedInstanceState) as Bundle
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        binding.chatList.apply {
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            this.adapter = chatAdapter_

            //scroll for last message
            //this.smoothScrollToPosition(-1)
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
        //chatAdapter.chatList.addAll(DummyData.getChatList())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putAll(bundle)
    }

    /** setListener For Input Box **/
    private fun setInputListener() {
        binding.sendBtn.setOnClickListener {
            if (binding.textInputEditText.text!!.isNotEmpty()) {
                val newChat = ChatDTO("김진우","PqbxywH1wjMHbkcWBfkD9annA5Z2", Date(),binding.textInputLayout.editText!!.text.toString(),"PqbxywH1wjMHbkcWBfkD9annA5Z2")
                binding.textInputLayout.editText!!.text.clear()
                chatViewModel.sendMsg(newChat)
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
        chatViewModel.detach(calcRoomDTO)
        chatAdapter_.stopListening()
        super.onStop()
    }

}