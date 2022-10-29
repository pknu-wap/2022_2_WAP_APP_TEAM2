package com.example.wapapp2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.wapapp2.model.CalculationItemDTO
import com.example.wapapp2.model.FriendDTO

class NewCalculationViewModel(application: Application) : AndroidViewModel(application) {
    private val friendsList: ArrayList<FriendDTO> = ArrayList<FriendDTO>()
    private val calculationItemList: ArrayList<CalculationItemDTO> = ArrayList<CalculationItemDTO>()

    val removeCalculationItemLiveData: MutableLiveData<Int> = MutableLiveData<Int>()

    val removeFriendLiveData: MutableLiveData<Int> = MutableLiveData<Int>()
    val friendsListLiveData: MutableLiveData<ArrayList<FriendDTO>> = MutableLiveData<ArrayList<FriendDTO>>()

    fun removeCalculationItem(position: Int) {
        calculationItemList.removeAt(position)
        removeCalculationItemLiveData.value = position
    }

    fun addCalculationItem(calculationItemDTO: CalculationItemDTO) {
        calculationItemList.add(calculationItemDTO)
    }

    fun addFriendDTOItem(friendsList: ArrayList<FriendDTO>) {
        friendsList.clear()
        friendsList.addAll(friendsList)
        friendsListLiveData.value = friendsList
    }

    fun removeFriendDTOItem(position: Int) {
        friendsList.removeAt(position)
    }

    fun calcTotalPrice(): String {
        var price = 0
        for (item in calculationItemList) {
            price += item.price
        }

        return price.toString()
    }
}