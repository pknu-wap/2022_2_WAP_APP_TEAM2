package com.example.wapapp2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wapapp2.firebase.FireStoreNames
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.model.ReceiptProductDTO
import com.example.wapapp2.repository.ReceiptImgRepositoryImpl
import com.example.wapapp2.repository.ReceiptRepositoryImpl
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlin.coroutines.resume

class ReceiptViewModel : ViewModel() {
    private val fireStore = FirebaseFirestore.getInstance()
    private val receiptRepository = ReceiptRepositoryImpl.INSTANCE
    private var currentMySummary = 0
    var currentRoomId: String? = null
    var currentReceiptId: String? = null
    var currentReceiptDTO: ReceiptDTO? = null

    val getCurrentSummary get() = currentMySummary

    val products = MutableLiveData<MutableList<ReceiptProductDTO>>()

    fun updateSummary_forNewProduct(productDTO: ReceiptProductDTO) {
        currentMySummary += try {
            productDTO.price / productDTO.personCount
        } catch (e: ArithmeticException) {
            0
        }
    }

    fun product_checked(productDTO: ReceiptProductDTO) {
        currentMySummary += productDTO.price / ++productDTO.personCount
        CoroutineScope(Dispatchers.Default).launch {
            receiptRepository.addMyID_fromProductParticipantIDs(productDTO.id)
        }
    }

    fun product_unchecked(productDTO: ReceiptProductDTO) {
        currentMySummary -= productDTO.price / productDTO.personCount--
        CoroutineScope(Dispatchers.Default).launch {
            receiptRepository.subMyID_fromProductParticipantIDs(productDTO.id)
        }
    }

    fun getCurrentSummary(): Int {
        return currentMySummary
    }


    fun getReceiptsRecyclerOptions(calcRoomId: String): FirestoreRecyclerOptions<ReceiptDTO> {
        val query = fireStore.collection(FireStoreNames.calc_rooms.name)
                .document(calcRoomId).collection(FireStoreNames.receipts.name).orderBy("status")

        val options = FirestoreRecyclerOptions.Builder<ReceiptDTO>()
                .setQuery(query, MetadataChanges.INCLUDE) {
                    val dto = it.toObject<ReceiptDTO>()!!
                    dto.id = it.id
                    dto
                }.build()
        return options
    }

    fun getProducts(receiptId: String, calcRoomId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = async {
                receiptRepository.getProducts(receiptId, calcRoomId)
            }
            result.await()
            withContext(Main) {
                products.value = result.await()
            }
        }
    }


}