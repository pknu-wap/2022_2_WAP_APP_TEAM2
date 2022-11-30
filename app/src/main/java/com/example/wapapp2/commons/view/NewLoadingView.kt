package com.example.wapapp2.commons.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Adapter
import android.widget.FrameLayout
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.databinding.ViewLoadingBinding
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.view.checkreceipt.ReceiptsAdapter
import com.firebase.ui.common.BaseChangeEventListener
import com.firebase.ui.firestore.FirestoreRecyclerAdapter


class NewLoadingView : FrameLayout, DefaultLifecycleObserver {
    private val binding: ViewLoadingBinding = ViewLoadingBinding.inflate(LayoutInflater.from(context), this, true)
    private val contentViews = mutableListOf<View>()
    private var succeed = false
    val isSuccess get() = succeed
    private var btnEnabled = false
    var emptyListMsg: String? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(
            context: Context, attrs: AttributeSet?, defStyleAttr: Int,
            defStyleRes: Int,
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }


    /**
     * contentView : 표시여부를 변경할 views,
     * 함수 호출과 함께 contentView들을 숨기고, 로딩바 표시
     */
    fun setContentView(emptyMsg: String, vararg contentView: View) {
        this.contentViews.addAll(contentView.toMutableList())
        emptyListMsg = emptyMsg
        onFailed(emptyMsg)
    }

    /**
     * 요청한 이벤트가 성공했을때 사용, 로딩뷰를 숨기고 지정힌 contextView들을 표시함
     */
    fun onSuccessful() {
        succeed = true
        for (v in contentViews)
            v.visibility = VISIBLE
        visibility = GONE
    }

    /**
     * 요청한 이벤트가 실패했을때 사용, 로딩뷰와 지정한 contentView들을 숨기고, 메시지를 표시
     * 버튼 추가하였을 경우에는 버튼도 표시
     */
    fun onFailed(text: String) {
        succeed = false
        for (v in contentViews)
            v.visibility = VISIBLE

        binding.status.text = text
        binding.status.visibility = VISIBLE
        binding.progressbar.visibility = GONE

        binding.btn.visibility = if (btnEnabled) VISIBLE else GONE
        visibility = VISIBLE
    }

    /**
     * 이벤트를 요청할때 사용
     */
    fun onStarted() {
        succeed = false
        for (v in contentViews)
            v.visibility = GONE

        binding.btn.visibility = GONE
        binding.status.visibility = GONE
        binding.progressbar.visibility = VISIBLE
        visibility = VISIBLE
    }

    fun setTextColor(color: Int) {
        binding.status.setTextColor(color)
    }

    fun setBtnOnClickListener(onClickListener: OnClickListener?) {
        binding.btn.setOnClickListener(onClickListener)
        binding.btn.visibility = VISIBLE
        btnEnabled = true
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)

        contentViews.clear()
    }



}