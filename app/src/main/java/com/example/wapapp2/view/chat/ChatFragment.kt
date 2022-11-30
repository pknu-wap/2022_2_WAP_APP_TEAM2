package com.example.wapapp2.view.chat

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.R
import com.example.wapapp2.commons.classes.DeviceUtils
import com.example.wapapp2.commons.classes.WrapContentLinearLayoutManager
import com.example.wapapp2.commons.view.NewLoadingView
import com.example.wapapp2.databinding.FragmentChatBinding
import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.repository.FcmRepositoryImpl
import com.example.wapapp2.view.calculation.CalcMainFragment
import com.example.wapapp2.viewmodel.ChatViewModel
import com.example.wapapp2.viewmodel.CurrentCalcRoomViewModel
import com.example.wapapp2.viewmodel.MyAccountViewModel
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.DocumentChange
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*


class ChatFragment : Fragment(), ChatDataObserver.NewMessageReceivedCallback, ChatDataObserver.OnFirstDataListListener {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private var chatAdapter: ChatPagingAdapter? = null

    private lateinit var viewHeightCallback: CalcMainFragment.ViewHeightCallback
    private val myAccountViewModel by activityViewModels<MyAccountViewModel>()
    private val currentCalcRoomViewModel by viewModels<CurrentCalcRoomViewModel>({ requireParentFragment() })
    private val chatViewModel by viewModels<ChatViewModel>({ requireParentFragment() })

    private var chatDataObserver: ChatDataObserver? = null
    private var initialized: Boolean = true

    private var keyboardOpen = false

    companion object {
        const val TAG = "ChatFragment"
    }


    private val chatListOnLayoutChangeListener =
            View.OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                Log.e("채팅리스트 OnLayoutChangeListener", "oldBottom : $oldBottom, bottom : $bottom, 키보드 상태 : ${
                    DeviceUtils.showingKeyboard
                    (requireContext())
                }")

            }

    fun setViewHeightCallback(callback: CalcMainFragment.ViewHeightCallback) {
        this.viewHeightCallback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatViewModel.roomId = currentCalcRoomViewModel.roomId!!

        RxJavaPlugins.setErrorHandler { Log.w("APP#", it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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

            addOnLayoutChangeListener(chatListOnLayoutChangeListener)

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

        currentCalcRoomViewModel.participantMap.observe(viewLifecycleOwner) {
            if (chatAdapter == null) {
                if (myAccountViewModel.myProfileData.value != null) {
                    val config = PagingConfig(20, 10, false)
                    val options = FirestorePagingOptions.Builder<ChatDTO>()
                            .setLifecycleOwner(this@ChatFragment.viewLifecycleOwner)
                            .setQuery(chatViewModel.getQueryForOption(currentCalcRoomViewModel.roomId!!), config) { snapshot ->
                                val id = snapshot.getString("senderId").toString()
                                val userName: String = if (currentCalcRoomViewModel.participantMap.value!!.containsKey(id))
                                    currentCalcRoomViewModel.participantMap.value!![id]!!.userName
                                else
                                    snapshot.getString("userName")!!

                                val chatDto = ChatDTO(userName, snapshot.getTimestamp("sendedTime")?.toDate(),
                                        snapshot.getString("msg").toString(), id, snapshot.getBoolean("notice")!!)
                                chatDto.id = snapshot.id

                                chatDto
                            }
                            .build()

                    chatAdapter = ChatPagingAdapter(myAccountViewModel.myProfileData.value!!.id, options)
                    chatAdapter?.apply {
                        chatDataObserver = ChatDataObserver(
                                binding.chatList,
                                binding.chatList.layoutManager as WrapContentLinearLayoutManager,
                                this, this,
                                this@ChatFragment
                        )
                        chatDataObserver!!.registerLoadingView(binding.loadingView, getString(R.string.empty_chats))
                        chatDataObserver!!.onChanged()
                        registerAdapterDataObserver(chatDataObserver!!)
                        loadStateListener()
                        binding.chatList.adapter = chatAdapter
                    }

                    // 채팅 내역이 있는지 확인
                    chatViewModel.isEmptyChats.observe(viewLifecycleOwner, object : Observer<Boolean> {
                        override fun onChanged(isEmpty: Boolean?) {
                            chatViewModel.isEmptyChats.removeObserver(this)

                            if (isEmpty!!) {
                                //채팅 내역이 비어있으면 스냅샷 연결
                                addChatListener()
                            }
                        }
                    })
                    chatViewModel.isEmptyChatList()

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
                        .toString(), myAccountViewModel.myProfileData.value!!.id, false)

                // 전송
                chatViewModel.sendMsg(newChat) { Toast.makeText(context, "네트워크 연결을 확인하세요!", Toast.LENGTH_SHORT).show() }
                chatViewModel.sendChat(newChat, currentCalcRoomViewModel.calcRoom.value!!)
                //입력 초기화
                binding.textInputEditText.text?.clear()
            } else {
                Toast.makeText(requireContext(), "메시지를 입력해주세요!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        //채팅방이 화면에 띄워진 상태 -> 알림 구독 해제
        FcmRepositoryImpl.unSubscribeToTopic(currentCalcRoomViewModel.roomId!!)
        chatAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        chatAdapter?.stopListening()
        // 방에서 나간 경우 -> 채팅 알림 구독 해제
        if (currentCalcRoomViewModel.exitFromRoom)
            FcmRepositoryImpl.unSubscribeToTopic(currentCalcRoomViewModel.roomId!!)
        else
            FcmRepositoryImpl.subscribeToTopic(currentCalcRoomViewModel.roomId!!)
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

    private fun addChatListener() {
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
    }

    /**
     * 리스트 스크롤이 끝이 아니면서 새 메시지를 받았을때 -> 화면에 표시
     */
    override fun onReceived() {
        if (!isVisible)
            return

        chatAdapter?.getLastChatDTO()?.apply {
            val alias = if (currentCalcRoomViewModel.participantMap.value!!.containsKey(senderId))
                currentCalcRoomViewModel.participantMap.value!![senderId]!!.userName
            else userName

            val msg = "$alias : $msg"
            binding.newMsgTv.text = msg
            binding.newMsgFrame.visibility = View.VISIBLE

            //레이아웃 클릭 시 리스트 스크롤 끝으로 이동
            binding.newMsgFrame.setOnClickListener {
                chatDataObserver?.scrollToBottom(0)
                resetNewMsgView()
            }
        }

    }

    private fun loadStateListener() {
        lifecycleScope.launch {
            chatAdapter!!.loadStateFlow.collectLatest { loadStates ->
                when (loadStates.refresh) {
                    is LoadState.Error -> {
                        chatAdapter?.retry()
                    }
                    is LoadState.Loading -> {
                        // The initial Load has begun
                        // ...
                    }
                    else -> {}
                }

                when (loadStates.append) {
                    is LoadState.Error -> {
                        chatAdapter?.retry()
                    }
                    is LoadState.Loading -> {
                        // The adapter has started to load an additional page
                        // ...
                    }
                    is LoadState.NotLoading -> {
                        if (loadStates.append.endOfPaginationReached) {
                            // The adapter has finished loading all of the data set
                            // ...
                        }
                        if (loadStates.refresh is LoadState.NotLoading) {
                            // The previous load (either initial or additional) completed
                            // ...
                        }
                    }
                }

            }
        }
    }

    private fun resetNewMsgView() {
        if (isVisible) {
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

    override fun onInsertedFirstDataList() {
        if (chatViewModel.listenerRegistration == null)
            addChatListener()
    }


    private fun RecyclerView.isScrollable(): Boolean {
        return canScrollVertically(1) || canScrollVertically(-1)
    }
}