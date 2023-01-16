package com.sunnyweather.android.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//为了能够使用它(Retrofit接口)，我们还得创建一个Retrofit构建器.
object ServiceCreator {

    private const val BASE_URL = "https://api.caiyunapp.com/"

    //在内部使用Retrofit.Builder
    //构建一个Retrofit对象，注意这些都是用private修饰符来声明的，相当于对于外部而言它们都
    //是不可见的。
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)
    inline fun <reified T> create(): T = create(T::class.java)
}