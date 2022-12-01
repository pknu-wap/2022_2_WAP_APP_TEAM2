package com.example.wapapp2.view.calculation.receipt.interfaces

import com.example.wapapp2.model.ReceiptProductDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface IProductCheckBox {
    fun updateMyTransferMoney(): Int

    fun onProductChecked(productDTO: ReceiptProductDTO)

    fun onProductUnchecked(productDTO: ReceiptProductDTO)
}