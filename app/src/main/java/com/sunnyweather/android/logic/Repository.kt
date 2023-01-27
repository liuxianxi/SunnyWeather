package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.dao.PlaceDao
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

//仓库层有点像是一个数据获取与缓存的中间层
//新建一个Repository单例类，作为仓库层的统一封装入口
object Repository {
/*
    //代码中我们还将liveData()函数的线程参数类型指定成了
    //Dispatchers.IO，这样代码块中的所有代码就都运行在子线程中了
    fun searchPlaces(query: String)  =  liveData(Dispatchers.IO) {
        val result = try {
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query )
            if (placeResponse.status == "ok") {
                //如果服务器响应的状态是ok，那么就使用Kotlin内置的Result.success()方法来包装获取的城市数据列表
                val places = placeResponse.places
                Result.success(places)
            }else{
                Result.failure(RuntimeException("response status is" +
                        "${placeResponse.status}"))
            }
        }catch (e: Exception) {
            Result.failure<List<Place>>(e)
        }
        //使用一个emit()方法将包装的结果发射出去.
        emit(result)
    }

    //显示天气信息

    fun refreshWeather(lng: String, lat: String) =  liveData(Dispatchers.IO) {
        val result = try {
            //由于async函数必须在协程作用域内才能调用，所以这里又使用coroutineScope函数创建了一个
            //协程作用域
            coroutineScope {
                //只需要分别在两个async函数中发起网络请求，然后再分别调用它们的await()
                //方法，就可以保证只有在两个网络请求都成功响应之后，才会进一步执行程序
                val deferredRealtime = async {
                    SunnyWeatherNetwork.getRealtimeWeather(lng, lat )
                }
                val deferredDaily = async {
                    SunnyWeatherNetwork.getDailyWeather(lng, lat)
                }
                val realtimeResponse = deferredRealtime.await()
                val dailyResponse = deferredDaily.await()
                //如果它们的响应状态都是ok，那么就将Realtime和Daily对象取出并封装到一个Weather对象
                //中，然后使用Result.success()方法来包装这个Weather对象，否则就使用
                //Result.failure()方法来包装一个异常信息，最后调用emit()方法将包装的结果发射出去。
                if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                    val weather = Weather(realtimeResponse.result.realtime,
                    dailyResponse.result.daily)
                    Result.success(weather)
                } else {
                    Result.failure(
                        java.lang.RuntimeException(
                            "realtime response status is ${realtimeResponse.status}" +
                        "daily response status is ${dailyResponse.status}"
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Result.failure<Weather>(e)
        }
        emit(result)
    }
 */

    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }


    //在仓库层我们并没有提供两个分别用于获取实时天气信息和未来天气信息的方法，而是
    //提供了一个refreshWeather()方法用来刷新天气信息。因为对于调用方而言，需要调用两次
    //请求才能获得其想要的所有天气数据明显是比较烦琐的行为，因此最好的做法就是在仓库层再
    //进行一次统一的封装。
    fun refreshWeather(lng: String, lat: String, placeName: String) = fire(Dispatchers.IO) {
        coroutineScope {
            val deferredRealtime = async {
                SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SunnyWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather = Weather(realtimeResponse.result.realtime,
                    dailyResponse.result.daily)
                Result.success(weather)
            } else {
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realtimeResponse.status}" +
                                "daily response status is ${dailyResponse.status}"
                    )
                )
            }
        }
    }
    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }

    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

}

