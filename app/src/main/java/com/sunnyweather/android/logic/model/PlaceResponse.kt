package com.sunnyweather.android.logic.model

import android.location.Location
import com.google.gson.annotations.SerializedName

 //通过这种方式，我们就能把全球绝大多数城市的数据信息获取到了。

//status代表请求的状态，ok表示成功
data class PlaceResponse(val status: String, val places: List<Place>)

//places是一个JSON数组，会包含几个与我们查询的关
//键字关系度比较高的地区信息。其中name表示该地区的名字，location表示该地区的经纬
//度，formatted_address表示该地区的地址。
data class Place(val name: String, val location:Location,
@SerializedName("formatted_address") val address: String)

data class Location(val lng: String, val lat: String)