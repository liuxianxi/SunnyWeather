package com.sunnyweather.android.logic.network

import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//定义一个用于访问彩云天气城市搜索API的Retrofit接口
interface PlaceService {

    //query参数指定的是要查询的关键字，token参数传入我们刚才申请到的令牌值即可。服务器
    //会返回我们一段JSON格式的数据，大致内容如下所示
    @GET("v2/place?token=${SunnyWeatherApplication.TOKEN}&lang = zh_CN")
    fun searchPlaces(@Query("query") query: String) : Call<PlaceResponse>
}