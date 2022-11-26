package com.example.wapapp2.view.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wapapp2.R
import com.example.wapapp2.commons.classes.WrapContentLinearLayoutManager
import com.example.wapapp2.commons.view.NewLoadingView
import com.example.wapapp2.databinding.FragmentChatBinding
import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.view.calculation.CalcMainFragment
import com.example.wapapp2.viewmodel.ChatViewModel
import com.example.wapapp2.viewmodel.CurrentCalcRoomViewModel
import com.example.wapapp2.viewmodel.MyAccountViewModel
import com.example.wapapp2.viewmodel.fcm.FcmViewModel
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.DocumentChange
import kotlinx.coroutines.launch
import java.util.*


class ChatFragment : Fragment(), ChatDataObserver.NewMessageReceivedCallback {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private var chatAdapter: ChatPagingAdapter? = null

    private lateinit var viewHeightCallback: CalcMainFragment.ViewHeightCallback
    private val chatViewModel by viewModels<ChatViewModel>()
    private val myAccountViewModel by activityViewModels<MyAccountViewModel>()
    private val currentCalcRoomViewModel by viewModels<CurrentCalcRoomViewModel>({ requireParentFragment() })
    private val fcmViewModel by viewModels<FcmViewModel>()

    private var chatDataObserver: ChatDataObserver? = null

    companion object {
        const val TAG = "ChatFragment"
    }


    fun setViewHeightCallback(callback: CalcMainFragment.ViewHeightCallback) {
        this.viewHeightCallback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fcmViewModel.subscribeToCalcRoomChat(currentCalcRoomViewModel.roomId!!)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        binding.loadingView.setContentView(binding.chatList)

        binding.chatList.apply {
            layoutManager = WrapContentLinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
            setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                //스크롤이 끝으로 이동한 경우 하단 채팅알림 지우기
                if (!isVisible)
                    return@setOnScrollChangeListener

                if (binding.newMsgFrame.visibility == View.VISIBLE) {
                    chatDataObserver?.apply {
                        if (atBottom(0)) {
                            resetNewMsgView()
                        }
                    }
                }
            }
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

        currentCalcRoomViewModel.calcRoom.observe(viewLifecycleOwner) { it ->
            if (chatAdapter == null) {
                chatViewModel.attach(it)
            }
        }

        currentCalcRoomViewModel.participants.observe(viewLifecycleOwner) {
            if (chatAdapter == null) {
                val config = PagingConfig(20, 10, false)
                val options = FirestorePagingOptions.Builder<ChatDTO>()
                        .setLifecycleOwner(this@ChatFragment.viewLifecycleOwner)
                        .setQuery(chatViewModel.getQueryForOption(currentCalcRoomViewModel.roomId!!), config) { snapshot ->
                            val id = snapshot.getString("senderId").toString()
                            val userName: String = if (currentCalcRoomViewModel.participantMap.containsKey(id))
                                currentCalcRoomViewModel.participantMap[id]!!.userName
                            else
                                snapshot.getString("userName")!!

                            ChatDTO(userName, snapshot.getTimestamp("sendedTime")?.toDate(),
                                    snapshot.getString("msg").toString(), id)
                        }
                        .build()

                if (myAccountViewModel.myProfileData.value != null) {
                    chatAdapter = ChatPagingAdapter(myAccountViewModel.myProfileData.value!!.id, options)
                    chatAdapter?.apply {
                        chatDataObserver = ChatDataObserver(
                                binding.chatList,
                                binding.chatList.layoutManager as LinearLayoutManager,
                                this::getAdapterItemCount,
                                this::checkLastMessageMine
                        )
                        chatDataObserver!!.registerLoadingView(binding.loadingView, getString(R.string.empty_chats))
                        chatDataObserver!!.onChanged()
                        registerAdapterDataObserver(chatDataObserver!!)
                        binding.chatList.adapter = chatAdapter
                    }

                    chatViewModel.addSnapshot(currentCalcRoomViewModel.roomId!!) { value, error ->
                        if (value != null) {
                            for (dc in value.documentChanges) {
                                if (dc.type == DocumentChange.Type.ADDED) {
                                    if (chatDataObserver!!.newMessageReceivedCallback == null)
                                        chatDataObserver!!.newMessageReceivedCallback = this@ChatFragment

                                    lifecycleScope.launch {
                                        chatAdapter!!.submitData(PagingData.from(value.documents))
                                    }
                                    break
                                }
                            }
                        }
                    }
                } else {  //내 아이디 안가져와짐 -> 네트워크 확인
                    binding.chatList.visibility = View.INVISIBLE
                    (binding.loadingView as NewLoadingView).apply {
                        onFailed("네트워크 연결을 확인하세요")
                    }
                }

            } else {
                chatAdapter?.refresh()
            }
        }
    }

    /** setListener For Input Box **/
    private fun setInputListener() {
        binding.sendBtn.setOnClickListener {
            if (binding.textInputEditText.text!!.isNotEmpty()) {
                val newChat = ChatDTO(myAccountViewModel.myProfileData.value!!.name, Date(), binding.textInputLayout.editText!!.text
                        .toString(), myAccountViewModel.myProfileData.value!!.id)

                // 전송
                chatViewModel.sendMsg(newChat) { Toast.makeText(context, "네트워크 연결을 확인하세요!", Toast.LENGTH_SHORT).show() }
                fcmViewModel.sendChat(newChat, currentCalcRoomViewModel.calcRoom.value!!)
                //입력 초기화
                binding.textInputEditText.text?.clear()
            } else {
                Toast.makeText(requireContext(), "메시지를 입력해주세요!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            chatAdapter?.stopListening()
        } else {
            chatAdapter?.startListening()
        }
    }

    /**
     * 리스트 스크롤이 끝이 아니면서 새 메시지를 받았을때 -> 화면에 표시
     */
    override fun onReceived() {
        if (!isVisible)
            return

        chatAdapter?.getLastChatDTO()?.apply {
            val alias = if (currentCalcRoomViewModel.participantMap.containsKey(senderId))
                currentCalcRoomViewModel.participantMap[senderId]!!.userName
            else
                userName

            val msg = "$alias : $msg"
            binding.newMsgTv.text = msg
            binding.newMsgFrame.visibility = View.VISIBLE

            val anim = binding.newMsgFrame.animate()
            anim.apply {
                duration = 3000
                withEndAction {
                    resetNewMsgView()
                }
                start()
            }

            //레이아웃 클릭 시 리스트 스크롤 끝으로 이동
            binding.newMsgFrame.setOnClickListener {
                chatDataObserver?.scrollToBottom(0)
                resetNewMsgView()
            }

        }

    }

    private fun resetNewMsgView() {
        if (isVisible) {
            binding.newMsgFrame.clearAnimation()

            binding.newMsgTv.text = ""
            binding.newMsgFrame.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        chatDataObserver?.apply {
            chatAdapter?.unregisterAdapterDataObserver(this)
        }
    }
}