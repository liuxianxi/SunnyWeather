package com.sunnyweather.android.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.ui.weather.WeatherActivity
import kotlinx.android.synthetic.main.activity_weather.*


//FruitAdapter中也有一个主构造函数,把要展示的数据源传进来
class PlaceAdapter(private val fragment: PlaceFragment, private val placeList: List<Place>) :
RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    //一个内部类ViewHolder，它要继承自
    //RecyclerView.ViewHolder。然后ViewHolder的主构造函数中要传入一个View参数，这
    //个参数通常就是RecyclerView子项的最外层布局，那么我们就可以通过findViewById()方
    //法来获取布局中placeName和placeAddress的实例了。
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val placeName: TextView = view.findViewById(R.id.placeName)
        val placeAddress: TextView = view.findViewById(R.id.placeAddress)

    }

//onCreateViewHolder()方法是用于创建ViewHolder实例的，我们在这个方法中将
//place_item布局加载进来，然后创建一个ViewHolder实例，并把加载出来的布局传入构造
//函数当中，最后将ViewHolder的实例返回。
override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item,
        parent, false)
    val holder = ViewHolder(view)
    holder.itemView.setOnClickListener {
        val position = holder.adapterPosition
        val place = placeList[position]

        val activity = fragment.activity
        if (activity is WeatherActivity) {
            activity.drawerLayout.closeDrawers()
            activity.viewModel.locationLng = place.location.lng
            activity.viewModel.locationLat = place.location.lat
            activity.viewModel.placeName = place.name
            activity.refreshWeather()
        } else {
            val intent = Intent(parent.context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            //在onCreateViewHolder()方法中，当点击了任何子项布局时，在跳转到
            //WeatherActivity之前，先调用PlaceViewModel的savePlace()方法来存储选中的城市
            // fragment.viewModel.savePlace(place)
            fragment.startActivity(intent)
            fragment.activity?.finish()
        }
        fragment.viewModel.savePlace(place)
    }
       // return ViewHolder(view)
    return holder
    }

    //onBindViewHolder()方法用于对
    //RecyclerView子项的数据进行赋值，会在每个子项被滚动到屏幕内的时候执行，这里我们通过
    //position参数得到当前项的Fruit实例，然后再将数据设置到ViewHolder的ImageView和
    //TextView当中即可。g
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.address
    }

    //getItemCount()方法就非常简单了，它用于告诉RecyclerView一共有
    //多少子项，直接返回数据源的长度就可以了。
    override fun getItemCount() = placeList.size
    }
