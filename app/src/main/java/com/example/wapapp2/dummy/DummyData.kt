package com.example.wapapp2.dummy

import com.example.wapapp2.R
import com.example.wapapp2.model.*
import com.example.wapapp2.view.login.Profiles
import org.joda.time.DateTime
import java.util.*
import kotlin.collections.ArrayList

class DummyData {
    companion object {


        fun getMyBankAccountList(name: String): ArrayList<BankAccountDTO> {
            val list = ArrayList<BankAccountDTO>()

            list.add(BankAccountDTO("", BankDTO("토스뱅크", R.drawable.ic_launcher_foreground, "22"), "100000076327", name, "0"))
            list.add(BankAccountDTO("", BankDTO("신한은행", R.drawable.ic_launcher_foreground, "18"), "110505621776", name, "0"))
            list.add(BankAccountDTO("", BankDTO("카카오뱅크", R.drawable.ic_launcher_foreground, "20"), "3333104213755", name, "0"))

            return list
        }


        fun getFixedDTOs(): ArrayList<FixedPayDTO> {
            val dummyData = ArrayList<FixedPayDTO>()
            dummyData.add(FixedPayDTO("", "김성윤", +6000, getMyBankAccountList("김성윤")))
            dummyData.add(FixedPayDTO("", "박준성", -24000, getMyBankAccountList("박준성")))
            return dummyData

        }

        fun getReceipts(): ArrayList<ReceiptDTO> {
            val dummyReceipts = ArrayList<ReceiptDTO>()

            val dummyReceipt1 = ReceiptDTO("1", null, "", null, "점심계산", "", false, 0, arrayListOf(), 0, DateTime.now())
            dummyReceipt1.addProduct(ReceiptProductDTO("", "돼지고기", 3600, 0, arrayListOf("1"), 0))
            dummyReceipt1.addProduct(ReceiptProductDTO("", "된장찌개", 3000, 0, arrayListOf("1"), 0))
            val dummyReceipt2 = ReceiptDTO("2", null, "", null, "저녁계산", "", false, 0, arrayListOf(), 0, DateTime.now())
            dummyReceipt2.addProduct(ReceiptProductDTO("", "숙소", 100000, 0, arrayListOf("1"), 0))
            dummyReceipt2.addProduct(ReceiptProductDTO("", "치킨", 20000, 0, arrayListOf("1"), 0))

            dummyReceipts.add(dummyReceipt1); dummyReceipts.add(dummyReceipt2)
            return dummyReceipts
        }

        fun getReceipt(): ReceiptDTO = ReceiptDTO("", null, "", null, "", "", false, 0, arrayListOf(), 0, DateTime.now())

    }

}