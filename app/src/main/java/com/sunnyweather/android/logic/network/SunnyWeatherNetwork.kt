package com.sunnyweather.android.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

//定义一个统一的网络数据源访问入口，对所有网络请求的API进行封装
object SunnyWeatherNetwork {

    //在SunnyWeatherNetwork这个网络数据源访问入口对新增的
    //WeatherService接口进行封装。
    private val weatherService = ServiceCreator.create(WeatherService::class.java)
    suspend fun getDailyWeather(lng: String, lat: String) =
        weatherService.getDailyWeather(lng, lat).await()
    suspend fun getRealtimeWeather(lng: String, lat: String) =
        weatherService.getRealtimeWeather(lng, lat).await()

    //ServiceCreator创建了一个PlaceService接口的动态代理对象
    private val placeService = ServiceCreator.create<PlaceService>()

    //定义了一个searchPlaces()函数，并在这里调用刚刚在PlaceService接口中定义的
    //searchPlaces()方法，以发起搜索城市数据请求。
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(
                        RuntimeException("response body is null")
                    )
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

                //首先await()函数仍然是一个挂起函
                //数，然后我们给它声明了一个泛型T，并将await()函数定义成了Call<T>的扩展函数，这样
                //所有返回值是Call类型的Retrofit网络请求接口就都可以直接调用await()函数了。
                //接着，await()函数中使用了suspendCoroutine函数来挂起当前协程，并且由于扩展函数的
                //原因，我们现在拥有了Call对象的上下文，那么这里就可以直接调用enqueue()方法让
                //Retrofit发起网络请求。接下来，使用同样的方式对Retrofit响应的数据或者网络请求失败的情
                //况进行处理就可以了。另外还有一点需要注意，在onResponse()回调当中，我们调用body()
                //方法解析出来的对象是可能为空的。如果为空的话，这里的做法是手动抛出一个异常，你也可
                //以根据自己的逻辑进行更加合适的处理。
            })
        }
    }
}