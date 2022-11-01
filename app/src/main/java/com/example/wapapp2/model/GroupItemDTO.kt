package com.example.wapapp2.model

/**
 * @param date : iso format date String
 * @param members : 참여자 이름 배열
 * @param inProgress : 진행중(True) / 정산완료(Flase)
 *  **/
class GroupItemDTO(val date: String, val members: ArrayList<String>, inProgress : Boolean) {
    val state = if (inProgress) "정산 진행중.." else "정산 완료"
}