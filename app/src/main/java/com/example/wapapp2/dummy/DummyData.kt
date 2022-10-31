package com.example.wapapp2.dummy

import com.example.wapapp2.R
import com.example.wapapp2.model.*
import com.example.wapapp2.view.login.Profiles
import org.joda.time.DateTime

class DummyData {
    companion object {
        fun getPeople(): ArrayList<CalcRoomMemberData> {
            val testList = ArrayList<CalcRoomMemberData>()
            testList.add(CalcRoomMemberData("0", "박준성"))
            testList.add(CalcRoomMemberData("1", "김진우"))
            testList.add(CalcRoomMemberData("2", "김성윤"))
            return testList
        }

        fun getRoom(): CalcRoomData {
            return CalcRoomData("0", "배달 정산", getPeople())
        }

        fun getChatList(): ArrayList<ChatData> {
            var dateTime = DateTime.now()
            val peopleList = getPeople()

            // test data
            val testList = ArrayList<ChatData>()
            testList.add(ChatData(peopleList[0].userName, peopleList[0].userId, dateTime.toString(), "안녕하세요"))

            dateTime = dateTime.plusMinutes(2)
            testList.add(ChatData(peopleList[1].userName, peopleList[1].userId, dateTime.toString(), "네!! 안녕하세요!"))
            dateTime = dateTime.plusMinutes(1)
            testList.add(ChatData(peopleList[2].userName, peopleList[2].userId, dateTime.toString(), "네!! 안녕하세요."))
            testList.add(ChatData(peopleList[2].userName, peopleList[2].userId, dateTime.toString(), "~~"))
            testList.add(ChatData(peopleList[0].userName, peopleList[0].userId, dateTime.toString(), "!"))
            testList.add(ChatData(peopleList[1].userName, peopleList[1].userId, dateTime.toString(), "@@"))
            testList.add(ChatData(peopleList[2].userName, peopleList[2].userId, dateTime.toString(), "@@"))
            testList.add(ChatData(peopleList[1].userName, peopleList[1].userId, dateTime.toString(), "@@"))

            return testList
        }

        fun getFriendsList(): ArrayList<FriendDTO> {
            val list = ArrayList<FriendDTO>()
            var uid = 0

            list.add(FriendDTO(uid++.toString(), "김성윤", "ksu8063@naver.com"))
            list.add(FriendDTO(uid++.toString(), "박준성", "jesp0305@naver.com"))
            list.add(FriendDTO(uid++.toString(), "김진우", "nbmlon99@naver.com"))
            list.add(FriendDTO(uid++.toString(), "남진하", "zinha@naver.com"))
            list.add(FriendDTO(uid.toString(), "옥수환", "oksu@naver.com"))

            return list
        }


        fun getMyBankAccountList(): ArrayList<BankAccountDTO> {
            val list = ArrayList<BankAccountDTO>()

            list.add(BankAccountDTO(BankDTO("토스뱅크", R.drawable.ic_launcher_foreground, "22"), "100000076327", "박준성"))
            list.add(BankAccountDTO(BankDTO("신한은행", R.drawable.ic_launcher_foreground, "18"), "110505621776", "박준성"))
            list.add(BankAccountDTO(BankDTO("카카오뱅크", R.drawable.ic_launcher_foreground, "20"), "3333104213755", "박준성"))

            return list

        fun getProfiles() : ArrayList<Profiles>{
            val dummyFriends = ArrayList<Profiles>()
            dummyFriends.add(Profiles(R.drawable.girl,"김진우 (나)","nbmlon99@naver.com"))
            dummyFriends.add(Profiles(R.drawable.man,"박준성","jesp0305@naver.com"))
            dummyFriends.add(Profiles(R.drawable.man,"김성윤","ksu8063@naver.com"))
            return dummyFriends
        }


        fun getGroupList() : ArrayList<GroupItemDTO> {
            return arrayListOf<GroupItemDTO>(
                GroupItemDTO(DateTime.now().minusDays(20).toString(), arrayListOf("김진우", "김성윤", "박준성"), false),
                GroupItemDTO(DateTime.now().minusDays(7).toString(), arrayListOf("짱구","훈이","유리","철수","훈이"), true)
            )
        }

        fun getFixedDTOs() : ArrayList<FixedPayDTO> {
            val dummyData = ArrayList<FixedPayDTO>()
            dummyData.add(FixedPayDTO("김성윤",+6000))
            dummyData.add(FixedPayDTO("박준성",-24000))
            return dummyData

        }
    }

}