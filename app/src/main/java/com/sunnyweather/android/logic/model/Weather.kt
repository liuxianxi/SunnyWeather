package com.sunnyweather.android.logic.model

//还需要在logic/model包下再定义一个Weather类，用于将Realtime和Daily对象封装起来
data class Weather(val realtime: RealtimeResponse.Realtime, val daily: DailyResponse.Daily)
