package com.sunnyweather.android.ui.weather

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.model.getSky
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.now.*
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    //在onCreate()方法中，首先从Intent中取出经
    //纬度坐标和地区名称，并赋值到WeatherViewModel的相应变量中
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //们调用了getWindow().getDecorView()方法拿到当前Activity的DecorView，再调用它
        //的setSystemUiVisibility()方法来改变系统UI的显示，
        val decorView = window.decorView
        decorView.systemUiVisibility =
            //这里传入View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN和
                // View.SYSTEM_UI_FLAG_LAYOUT_STABLE就表示Activity的布局会显示在状态栏上面
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        //调用一下setStatusBarColor()方法将状态栏设置成透明色
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_weather)
        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty() ) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息" , Toast.LENGTH_SHORT) .show()
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
        })
      //  viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        //调用了SwipeRefreshLayout的
        //setColorSchemeResources()方法，来设置下拉刷新进度条的颜色
        swipeRefresh.setColorSchemeResources(R.color.purple_200)
        refreshWeather()
        //调用setOnRefreshListener()
        //方法给SwipeRefreshLayout设置一个下拉刷新的监听器，当触发了下拉刷新操作的时候，就
        //在监听器的回调中调用refreshWeather()方法来刷新天气信息
        swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }

        //：第一，在切换城市按钮的点击事件中调用DrawerLayout的
        //openDrawer()方法来打开滑动菜单
        navBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        //第二，监听DrawerLayout的状态，当滑动菜单被隐藏
        //的时候，同时也要隐藏输入法。之所以要做这样一步操作，是因为待会我们在滑动菜单中搜索
        //城市时会弹出输入法，而如果滑动菜单隐藏后输入法却还显示在界面上，就会是一种非常怪异的情况。
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE)as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS)
            }
        })
    }
//在这里调用WeatherViewModel的refreshWeather()方法，
//并将SwipeRefreshLayout的isRefreshing属性设置成true，从而让下拉刷新进度条显示出来
    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        swipeRefresh.isRefreshing = true
    }



    private fun showWeatherInfo(weather: Weather) {
        placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        // 填充now.xml布局中的数据
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        currentTemp.text = currentTempText
        currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        // 填充forecast.xml布局中的数据
        forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                forecastLayout, false)
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            forecastLayout.addView(view)
        }
        // 填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE



    }
}