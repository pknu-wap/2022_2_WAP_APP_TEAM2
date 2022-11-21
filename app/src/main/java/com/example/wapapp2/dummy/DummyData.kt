package com.example.wapapp2.dummy

import androidx.core.app.NotificationCompat.getPeople
import com.example.wapapp2.R
import com.example.wapapp2.model.*
import com.example.wapapp2.view.login.Profiles
import org.joda.time.DateTime
import java.util.*
import kotlin.collections.ArrayList

class DummyData {
    companion object {


        fun getRoom(): CalcRoomDTO {
            return CalcRoomDTO(
                    Date(), Date(), "", arrayListOf("0"), arrayListOf("0", "1"), arrayListOf("0"),
                    CalcRoomDTO.RecentMsg("", "", Date(), ""), "정산방", arrayListOf())
        }


        fun getMyFriendsList(): ArrayList<FriendDTO> {
            val list = ArrayList<FriendDTO>()
            var uid = 0

            list.add(FriendDTO(uid++.toString(), "김성윤", "김성윤", "ksu8063@naver.com"))
            list.add(FriendDTO(uid++.toString(), "박준성", "박준성", "jesp0305@naver.com"))
            list.add(FriendDTO(uid++.toString(), "김진우", "김진우", "nbmlon99@naver.com"))
            list.add(FriendDTO(uid++.toString(), "짱구", "짱구", "ZZang9@naver.com"))
            list.add(FriendDTO(uid++.toString(), "훈이", "훈이", "huni@naver.com"))
            list.add(FriendDTO(uid++.toString(), "액션가면", "액션가면", "mask@naver.com"))
            list.add(FriendDTO(uid++.toString(), "원장님", "원장님", "ceo@naver.com"))
            list.add(FriendDTO(uid.toString(), "오수", "오수", "five@naver.com"))

            return list
        }


        fun getFriendsInRoomList(): ArrayList<FriendDTO> {
            val list = ArrayList<FriendDTO>()
            var uid = 0

            list.add(FriendDTO(uid++.toString(), "김성윤", "김성윤", "ksu8063@naver.com"))
            list.add(FriendDTO(uid++.toString(), "박준성", "박준성", "jesp0305@naver.com"))
            list.add(FriendDTO(uid.toString(), "김진우", "김진우", "nbmlon99@naver.com"))

            return list
        }

        fun getMyBankAccountList(name: String): ArrayList<BankAccountDTO> {
            val list = ArrayList<BankAccountDTO>()

            list.add(BankAccountDTO("", BankDTO("토스뱅크", R.drawable.ic_launcher_foreground, "22"), "100000076327", name, "0"))
            list.add(BankAccountDTO("", BankDTO("신한은행", R.drawable.ic_launcher_foreground, "18"), "110505621776", name, "0"))
            list.add(BankAccountDTO("", BankDTO("카카오뱅크", R.drawable.ic_launcher_foreground, "20"), "3333104213755", name, "0"))

            return list
        }

        fun getProfiles(): ArrayList<Profiles> {
            val dummyFriends = ArrayList<Profiles>()
            dummyFriends.add(Profiles(R.drawable.girl, "김진우 (나)", "nbmlon99@naver.com"))
            dummyFriends.add(Profiles(R.drawable.man, "박준성", "jesp0305@naver.com"))
            dummyFriends.add(Profiles(R.drawable.man, "김성윤", "ksu8063@naver.com"))
            return dummyFriends
        }


        fun getFixedDTOs(): ArrayList<FixedPayDTO> {
            val dummyData = ArrayList<FixedPayDTO>()
            dummyData.add(FixedPayDTO("", "김성윤", +6000, getMyBankAccountList("김성윤")))
            dummyData.add(FixedPayDTO("", "박준성", -24000, getMyBankAccountList("박준성")))
            return dummyData

        }

        fun getReceipts(): ArrayList<ReceiptDTO> {
            val dummyReceipts = ArrayList<ReceiptDTO>()

            val dummyReceipt1 = ReceiptDTO("1", null, "", null, "점심계산", "", false, 0, arrayListOf(), 0, DateTime.now().toString(), null)
            dummyReceipt1.addProduct(ReceiptProductDTO("", "돼지고기", 3600, 0, arrayListOf("1"), 0))
            dummyReceipt1.addProduct(ReceiptProductDTO("", "된장찌개", 3000, 0, arrayListOf("1"), 0))
            val dummyReceipt2 = ReceiptDTO("2", null, "", null, "저녁계산", "", false, 0, arrayListOf(), 0, DateTime.now().toString(), null)
            dummyReceipt2.addProduct(ReceiptProductDTO("", "숙소", 100000, 0, arrayListOf("1"), 0))
            dummyReceipt2.addProduct(ReceiptProductDTO("", "치킨", 20000, 0, arrayListOf("1"), 0))

            dummyReceipts.add(dummyReceipt1); dummyReceipts.add(dummyReceipt2)
            return dummyReceipts
        }

        fun getReceipt(): ReceiptDTO = ReceiptDTO("", null, "", null, "", "", false, 0, arrayListOf(), 0, DateTime.now().toString(), null)

        val testCalcRoomId = "LvJY5fz6TjlTDaHHX53l"
    }

}