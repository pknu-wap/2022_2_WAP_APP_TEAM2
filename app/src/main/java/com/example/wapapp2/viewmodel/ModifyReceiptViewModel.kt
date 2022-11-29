package com.example.wapapp2.viewmodel

import androidx.lifecycle.ViewModel
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.model.ReceiptProductDTO
import com.example.wapapp2.repository.CalcRoomRepositorylmpl
import com.example.wapapp2.repository.ReceiptImgRepositoryImpl
import com.example.wapapp2.repository.ReceiptRepositoryImpl
import kotlinx.coroutines.*

class ModifyReceiptViewModel : ViewModel() {
    private val receiptRepositoryImpl = ReceiptRepositoryImpl.INSTANCE
    private val receiptImgRepositoryImpl = ReceiptImgRepositoryImpl.INSTANCE
    private val calcRoomReceiptRepositoryImpl = CalcRoomRepositorylmpl.getINSTANCE()

    lateinit var originalReceiptDTO: ReceiptDTO
    lateinit var modifiedReceiptDTO: ReceiptDTO

    val originalProductMap = mutableMapOf<String, ReceiptProductDTO>()
    val modifiedProductMap = mutableMapOf<String, ReceiptProductDTO>()

    var currentRoomId: String? = null

    var receiptImgChanged = false
    var hasReceiptImg = false

    fun setOriginalProducts(list: MutableList<ReceiptProductDTO>) {
        for (product in list) {
            val copiedDto = product.copy(id = product.id,
                    name = product.name, price = product.price, count = product.count)

            originalProductMap[product.id] = product
            modifiedProductMap[product.id] = copiedDto
        }
    }

    fun modifyReceipt(calcRoomId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            //수정 내역을 map으로 저장
            //수정가능 자료 : name, status(정산상태), img_url, totalMoney
            val map = mutableMapOf<String, Any?>()
            val receiptId = originalReceiptDTO.id

            if (originalReceiptDTO.name != modifiedReceiptDTO.name)
                map["name"] = modifiedReceiptDTO.name
            if (originalReceiptDTO.totalMoney != modifiedReceiptDTO.totalMoney)
                map["totalMoney"] = modifiedReceiptDTO.totalMoney


            //사진이 변경된 경우
            if (receiptImgChanged) {
                // 기존에 사진이 있는 경우 삭제
                if (originalReceiptDTO.imgUrl.isNotEmpty())
                    receiptImgRepositoryImpl.deleteReceiptImg(originalReceiptDTO.imgUrl)

                // 새 사진 업로드
                if (modifiedReceiptDTO.imgUriInMyPhone != null) {
                    val uploadImg =
                            async { receiptImgRepositoryImpl.uploadReceiptImg(modifiedReceiptDTO.imgUriInMyPhone!!, calcRoomId) }
                    uploadImg.await()?.apply {
                        // 사진 업로드 성공
                        map["imgUrl"] = this
                    }
                    receiptRepositoryImpl.modifyReceipt(map, calcRoomId, receiptId)
                } else {
                    // 사진을 삭제한 경우
                    map["imgUrl"] = ""
                    receiptRepositoryImpl.modifyReceipt(map, calcRoomId, receiptId)
                }

            } else {
                if (map.isNotEmpty())
                    receiptRepositoryImpl.modifyReceipt(map, calcRoomId, receiptId)
            }
        }
    }

    /**
     * 영수증 수정 로직
     */
    fun modifyProducts(calcRoomId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // 삭제된 항목 집합   키 : id
            val removedSet =
                    originalProductMap.keys.toMutableSet().subtract(modifiedProductMap.keys.toMutableSet())

            // 추가된 항목 집합
            val addedSet =
                    modifiedProductMap.keys.toMutableSet().subtract(originalProductMap.keys.toMutableSet())

            // 추가된 항목 맵   키 : id
            val addedList = mutableListOf<ReceiptProductDTO>()

            // 수정되었을 가능성이 있는 모든 항목 집합
            val modifiedSet =
                    modifiedProductMap.keys.toMutableSet().intersect(originalProductMap.keys.toMutableSet())

            // 수정된 항목 맵   키 : id
            val modifiedMap = mutableMapOf<String, ReceiptProductDTO>()

            // 수정가능 자료 : 이름, 수량 ,단가
            // 수정된 항목 분석
            for (id in modifiedSet) {
                //수정 여부 확인
                if (!originalProductMap[id]!!.equalsSimple(modifiedProductMap[id]!!)) {
                    //수정 됨
                    modifiedMap[id] = modifiedProductMap[id]!!
                }
            }

            // 추가된 항목 분석
            for (id in addedSet) {
                addedList.add(modifiedProductMap[id]!!)
            }

            val receiptId = originalReceiptDTO.id

            if (modifiedMap.isNotEmpty())
                receiptRepositoryImpl.modifyProducts(modifiedMap, currentRoomId!!, receiptId)
            if (addedList.isNotEmpty())
                receiptRepositoryImpl.addProducts(receiptId, addedList, currentRoomId!!)
            if (removedSet.isNotEmpty())
                receiptRepositoryImpl.removeProducts(currentRoomId!!, receiptId, removedSet.toMutableList())
        }
    }

    fun removeReceipt(calcRoomId: String, receiptDTO: ReceiptDTO) {
        CoroutineScope(Dispatchers.IO).launch {
            //영수증 삭제
            receiptRepositoryImpl.removeReceipt(calcRoomId, receiptDTO.id)

            //진행중인 영수증 개수 가져오기기
            val onGoingReceiptCountsResult = async {
                calcRoomReceiptRepositoryImpl.getOngoingReceiptCounts(calcRoomId)
            }
            val onGoingReceiptCounts = onGoingReceiptCountsResult.await()
            //정산 상태 변경, 진행중인 정산이 없으면 false로, 그 외 -> true
            calcRoomReceiptRepositoryImpl.updateCalculationStatus(calcRoomId, onGoingReceiptCounts != 0)

            //영수증 사진 삭제
            if (receiptDTO.imgUrl.isNotEmpty()) {
                receiptImgRepositoryImpl.deleteReceiptImg(receiptDTO.imgUrl!!)
            }
        }
    }

    fun calcTotalPrice(): String {
        var price = 0

        for (product in modifiedProductMap.values) {
            price += (product.price * product.count)
        }

        modifiedReceiptDTO.totalMoney = price
        return price.toString()
    }
}