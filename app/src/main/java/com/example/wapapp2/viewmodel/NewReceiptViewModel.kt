package com.example.wapapp2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.model.ReceiptProductDTO
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.repository.ReceiptImgRepositoryImpl
import com.example.wapapp2.repository.ReceiptRepositoryImpl
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import org.joda.time.DateTime

class NewReceiptViewModel : ViewModel() {
    private val receiptRepository = ReceiptRepositoryImpl.INSTANCE
    private val receiptImgRepositoryImpl = ReceiptImgRepositoryImpl.INSTANCE
    private val receiptMap = HashMap<String, ReceiptDTO>()

    val removeReceiptLiveData: MutableLiveData<String> = MutableLiveData<String>()

    val addReceiptResult = MutableLiveData<Boolean>()

    var calcRoomId: String? = "LvJY5fz6TjlTDaHHX53l"
    lateinit var myName: String

    fun removeProduct(receiptId: String, receiptProductDTO: ReceiptProductDTO): Int {
        try {
            return receiptMap[receiptId]!!.removeProduct(receiptProductDTO)
        } catch (e: Exception) {
            throw e
        }
    }

    private fun addReceipt(receiptList: MutableList<ReceiptDTO>, calcRoomId: String) {
        CoroutineScope(Dispatchers.Default).launch {
            var count = 0

            for (receipt in receiptList) {
                //영수증 사진 있는 경우 추가
                receipt.imgUriInMyPhone?.also {
                    val imgFileName = async {
                        receiptImgRepositoryImpl.uploadReceiptImg(it, calcRoomId)
                    }

                    imgFileName.await()?.apply {
                        receipt.imgUrl = this
                    }
                }

                //영수증 추가
                val addReceiptResult = async {
                    receiptRepository.addReceipt(receipt, calcRoomId)
                }

                if (addReceiptResult.await()) {
                    //정산방 영수증 문서들중 마지막 아이템을 가져오기
                    val lastDocumentId = async {
                        receiptRepository.getLastDocumentId(calcRoomId)
                    }

                    if (lastDocumentId.await() != null) {
                        //영수증 항목 추가
                        val addProductsResult = async {
                            receiptRepository.addProducts(lastDocumentId.await().toString(), receipt.getProducts(), calcRoomId)
                        }

                        addProductsResult.await()
                        withContext(Main) {
                            if (++count == receiptList.size)
                                this@NewReceiptViewModel.addReceiptResult.value = addProductsResult.await()
                        }
                    } else {
                        withContext(Main) {
                            if (++count == receiptList.size)
                                this@NewReceiptViewModel.addReceiptResult.value = false
                        }
                    }
                } else {
                    withContext(Main) {
                        if (++count == receiptList.size)
                            this@NewReceiptViewModel.addReceiptResult.value = false
                    }
                }
            }
        }
    }

    fun addAllReceipts() {
        addReceipt(receiptMap.values.toMutableList(), calcRoomId!!)
    }

    fun addReceipt(receiptId: String) {
        receiptMap[receiptId] = ReceiptDTO().apply {
            id = receiptId
            payersName = myName
        }
    }

    fun addProduct(receiptId: String): ReceiptProductDTO {
        val receiptProductDTO = ReceiptProductDTO("", "", 0, 1, arrayListOf(), 0)
        receiptMap[receiptId]!!.addProduct(receiptProductDTO)
        return receiptProductDTO
    }

    fun removeReceipt(receiptId: String): Boolean {
        //영수증이 하나인 경우에는 삭제 불가 -> 수정해서 바꿔야 함
        return if (receiptMap.size == 1) {
            false
        } else {
            receiptMap.remove(receiptId)
            removeReceiptLiveData.value = receiptId
            true
        }
    }

    fun getReceipts(): MutableCollection<ReceiptDTO> = receiptMap.values

    fun getReceiptSet(): MutableSet<String> = receiptMap.keys

    fun getReceiptDTO(receiptId: String): ReceiptDTO? = receiptMap[receiptId]

    fun getProductCount(receiptId: String): Int = receiptMap[receiptId]?.getProducts()?.size ?: 0

    fun getReceiptCount(): Int = receiptMap.size

    fun calcTotalPrice(receiptId: String): String {
        var price = 0
        for (product in receiptMap[receiptId]!!.getProducts()) {
            price += (product.price * product.count)
        }

        receiptMap[receiptId]!!.totalMoney = price
        return price.toString()
    }

    fun calcTotalPrice(): String {
        var price = 0
        for (product in receiptMap.values) {
            price += product.totalMoney
        }

        return price.toString()
    }


}