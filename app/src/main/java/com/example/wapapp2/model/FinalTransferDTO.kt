package com.example.wapapp2.model

data class FinalTransferDTO(
        val payersId: String,
        val payersName: String,
        val transferMoney: Int,
        val accounts: MutableList<BankAccountDTO> = mutableListOf<BankAccountDTO>(),
)