package com.sunnyweather.android.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.Place
//ViewModel相当于逻辑层和
//UI层之间的一个桥梁
class PlaceViewModel : ViewModel() {

    //PlaceViewModel中也定义了一个
    //searchPlaces()方法，但是这里并没有直接调用仓库层中的searchPlaces()方法，而是将
    //传入的搜索参数赋值给了一个searchLiveData对象，并使用Transformations的
    //switchMap()方法来观察这个对象，否则仓库层返回的LiveData对象将无法进行观察
    private val searchLiveData = MutableLiveData<String>()

    //用于对界面上显示的城市数据进行缓存，
    val placeList = ArrayList<Place>()

    val placeLiveData = Transformations.switchMap(searchLiveData) { query ->
        Repository.searchPlaces(query)
    }

    fun searchPlaces(query: String) {
        searchLiveData.value = query
    }
    //现在每当searchPlaces()函数被调用时，
    //switchMap()方法所对应的转换函数就会执行。然后在转换函数中，我们只需要调用仓库层中
    //定义的searchPlaces()方法就可以发起网络请求，同时将仓库层返回的LiveData对象转换成
    //一个可供Activity观察的LiveData对象。

    fun savePlace(place: Place) = Repository.savePlace(place)

    fun getSavePlace() = Repository.getSavedPlace()

    fun isPlaceSaved() = Repository.isPlaceSaved()
}