package com.example.wapapp2.dummy

import com.example.wapapp2.model.CalcRoomData
import com.example.wapapp2.model.CalcRoomMemberData
import com.example.wapapp2.model.ChatData
import org.joda.time.DateTime

class CalcRoomDummyData {
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

            return testList
        }
    }

}