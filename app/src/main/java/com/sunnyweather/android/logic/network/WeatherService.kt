package com.sunnyweather.android.logic.network

import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.DailyResponse
import com.sunnyweather.android.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {

    //：getRealtimeWeather()方法用于获取实时的天气信息，
    // getDailyWeather()方法用于获取未来的天气信息。
    @GET("v2.5/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng") lng: String, @Path("lat") lat: String):
            Call<RealtimeResponse>

    //使用了@Path注解来向请求接口中动态传入经
    //纬度的坐标。这两个方法的返回值分别被声明成了Call<RealtimeResponse>和
    //Call<DailyResponse>，对应了刚刚定义好的两个数据模型类
    @GET("v2.5/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng") lng: String, @Path("lat") lat: String):
            Call<DailyResponse>


}