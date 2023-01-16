package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers

//仓库层有点像是一个数据获取与缓存的中间层
//新建一个Repository单例类，作为仓库层的统一封装入口
object Repository {

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
}