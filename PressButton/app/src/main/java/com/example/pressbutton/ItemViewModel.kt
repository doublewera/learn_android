package com.example.pressbutton

import android.location.Location
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ItemViewModel : ViewModel() {
    //private val mutableSelectedItem = MutableLiveData<Item>()
    //val selectedItem: LiveData<Item> get() = mutableSelectedItem
    var myLocation: Location? = Location("")
    var locationIsSet = false
    /*fun selectItem(item: Item) {
        mutableSelectedItem.value = item
    }*/
}