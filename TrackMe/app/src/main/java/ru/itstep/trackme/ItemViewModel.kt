package com.example.trackme

import android.location.Location
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ItemViewModel : ViewModel() {
    //private val mutableSelectedItem = MutableLiveData<Item>()
    //val selectedItem: LiveData<Item> get() = mutableSelectedItem
    var data = ""
    var myLocation: Location? = Location("")
    var locationIsSet = false
    var running = false
    /*fun selectItem(item: Item) {
        mutableSelectedItem.value = item
    }*/
}