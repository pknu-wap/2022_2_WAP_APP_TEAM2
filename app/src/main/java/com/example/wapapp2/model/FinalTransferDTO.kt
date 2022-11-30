package com.example.wapapp2.model

data class FinalTransferDTO(
        var payersId: String,
        var payersName: String,
        var transferMoney: Int,
        var totalMoney: Int,
        val accounts: MutableList<BankAccountDTO> = mutableListOf<BankAccountDTO>(),
)