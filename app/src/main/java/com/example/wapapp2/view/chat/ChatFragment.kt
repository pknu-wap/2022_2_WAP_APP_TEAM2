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
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wapapp2.R
import com.example.wapapp2.commons.classes.ListAdapterDataObserver
import com.example.wapapp2.databinding.FragmentChatBinding
import com.example.wapapp2.model.ChatDTO
import com.example.wapapp2.view.calculation.CalcMainFragment
import com.example.wapapp2.viewmodel.ChatViewModel
import com.example.wapapp2.viewmodel.CurrentCalcRoomViewModel
import com.example.wapapp2.viewmodel.MyAccountViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.MetadataChanges
import java.util.*


class ChatFragment : Fragment(), ScrollListener {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private var chatAdapter: ChatPagingAdapter? = null

    private lateinit var viewHeightCallback: CalcMainFragment.ViewHeightCallback
    private val chatViewModel by viewModels<ChatViewModel>()
    private val myAccountViewModel by activityViewModels<MyAccountViewModel>()
    private val currentCalcRoomViewModel by viewModels<CurrentCalcRoomViewModel>({ requireParentFragment() })

    companion object {
        const val TAG = "ChatFragment"
    }


    fun setViewHeightCallback(callback: CalcMainFragment.ViewHeightCallback) {
        this.viewHeightCallback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        binding.loadingView.setContentView(binding.chatList)

        binding.chatList.apply {
            val lm = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            //lm.stackFromEnd = true
            layoutManager = lm
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
        currentCalcRoomViewModel.calcRoom.observe(viewLifecycleOwner) {
            if (chatAdapter == null) {
                chatViewModel.attach(it)

                val options = FirestoreRecyclerOptions.Builder<ChatDTO>()
                        .setLifecycleOwner(this@ChatFragment)
                        .setQuery(chatViewModel.getQueryForOption(it),
                                MetadataChanges.INCLUDE) { snapshot ->
                            ChatDTO(snapshot.getString("userName").toString(), snapshot.getTimestamp("sendedTime")?.toDate(),
                                    snapshot.getString("msg").toString(), snapshot.getString("senderId").toString())
                        }
                        .build()

                chatAdapter = ChatPagingAdapter(this@ChatFragment, myAccountViewModel.myProfileData.value!!.id, options)

                chatAdapter?.apply {
                    val adapterObserver = ListAdapterDataObserver(binding.chatList, binding.chatList.layoutManager as LinearLayoutManager,
                            this)
                    adapterObserver.registerLoadingView(binding.loadingView, getString(R.string.empty_chats))
                    registerAdapterDataObserver(adapterObserver)
                    binding.chatList.adapter = chatAdapter
                }

            }
        }
    }

    /** setListener For Input Box **/
    private fun setInputListener() {
        binding.sendBtn.setOnClickListener {
            if (binding.textInputEditText.text!!.isNotEmpty()) {
                val newChat = ChatDTO(myAccountViewModel.myProfileData.value!!.name, Date(), binding.textInputLayout.editText!!.text
                        .toString(), myAccountViewModel.myProfileData.value!!.id)

                binding.textInputLayout.editText!!.text.clear()
                // 전송
                chatViewModel.sendMsg(newChat)
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

    override fun scrollToBottom() {
        binding.chatList.scrollToPosition(binding.chatList.adapter!!.itemCount - 1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}