package com.example.wapapp2.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.model.ReceiptProductDTO
import com.example.wapapp2.repository.ReceiptImgRepositoryImpl
import com.example.wapapp2.repository.ReceiptRepositoryImpl
import kotlinx.coroutines.*

class ModifyReceiptViewModel : ViewModel() {
    private val receiptRepositoryImpl = ReceiptRepositoryImpl.INSTANCE
    private val receiptImgRepositoryImpl = ReceiptImgRepositoryImpl.INSTANCE

    lateinit var originalReceiptDTO: ReceiptDTO
    var modifiedReceiptDTO : ReceiptDTO? = null

    var receiptImgChanged = false
    var hasReceiptImg = false

    fun modifyReceipt( calcRoomId: String) {
        CoroutineScope(Dispatchers.Default).launch {
            //수정 내역을 map으로 저장
            //수정가능 자료 : name, status(정산상태), img_url
            val map = HashMap<String, Any?>()
            if (originalReceiptDTO.name != modifiedReceiptDTO.name)
                map["name"] = modifiedReceiptDTO.name
            if (originalReceiptDTO.status != modifiedReceiptDTO.status)
                map["status"] = modifiedReceiptDTO.status
            if (originalReceiptDTO.imgUrl != modifiedReceiptDTO.imgUrl)
                map["imgUrl"] = modifiedReceiptDTO.imgUrl

            val result = async { receiptRepositoryImpl.modifyReceipt(map, calcRoomId) }
            result.await()
        }
    }

    fun modifyProducts(productList: ArrayList<HashMap<String, ReceiptProductDTO>>, calcRoomId: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val list = ArrayList<HashMap<String, Any?>>()
            var originalProductDTO: ReceiptProductDTO? = null
            var newProductDTO: ReceiptProductDTO? = null

            for (map in productList) {
                val modifiedMap = HashMap<String, Any?>()
                originalProductDTO = map["original"]
                newProductDTO = map["new"]
                //수정가능 자료 : 체크유저id목록, 이름, 가격

                if (originalProductDTO?.name != newProductDTO?.name)
                    modifiedMap["name"] = newProductDTO?.name
                if (originalProductDTO?.price != newProductDTO?.price)
                    modifiedMap["price"] = newProductDTO?.price

                modifiedMap["checkedUserIds"] = newProductDTO?.checkedUserIds
                modifiedMap["id"] = originalProductDTO?.id

                list.add(modifiedMap)
            }

            val result = async { receiptRepositoryImpl.modifyProducts(list, calcRoomId) }
            result.await()

        }
    }

    fun modifyReceiptImg(originalImgFileName: String, newImgUri: Uri, calcRoomId: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val deleteResult = async { receiptImgRepositoryImpl.deleteReceiptImg(originalImgFileName) }
            deleteResult.await()
            val uploadResult = async { receiptImgRepositoryImpl.uploadReceiptImg(newImgUri, calcRoomId) }
            uploadResult.await()

        }
    }

    fun deleteReceipt(calcRoomId: String, receiptId: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val deleteResult = async {
                receiptRepositoryImpl.deleteReceipt(calcRoomId, receiptId)
            }
            deleteResult.await()

        }
    }
}