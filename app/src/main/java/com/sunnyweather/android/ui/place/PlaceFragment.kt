package com.sunnyweather.android.ui.place

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunnyweather.android.R
import com.sunnyweather.android.ui.weather.WeatherActivity
import kotlinx.android.synthetic.main.fragment_place.*

class PlaceFragment : Fragment() {

    //这里使用了lazy函数这种懒加载技术来获取PlaceViewModel的实例，这是一种非常棒
    //的写法，允许我们在整个类中随时使用viewModel这个变量，而完全不用关心它何时初始化、
    //是否为空等前提条件。
    val viewModel by lazy { ViewModelProvider(this).get(PlaceViewModel::class.java) }

    private lateinit var adapter: PlaceAdapter

    //接下来在onCreateView()方法中加载了前面编写的fragment_place布局，这是Fragment
    //的标准用法，没什么需要解释的。
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //里在PlaceFragment中进行了判断，如果当前已有存储的城市数据，那么就获取已存储的数
        //据并解析成Place对象，然后使用它的经纬度坐标和城市名直接跳转并传递给
        //WeatherActivity，这样用户就不需要每次都重新搜索并选择城市了。
        if (viewModel.isPlaceSaved()) {
            val place = viewModel.getSavePlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this,viewModel.placeList)
        recyclerView.adapter = adapter

        //调用了EditText的addTextChangedListener()方法来监听搜索框内容的变化情况。每当
        //搜索框中的内容发生了变化，我们就获取新的内容，然后传递给PlaceViewModel的
        //searchPlaces()方法，这样就可以发起搜索城市数据的网络请求了。
        searchPlaceEdit.addTextChangedListener   { editable ->
            val content =  editable.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            }else {
                recyclerView.visibility=  View.GONE
                bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.placeLiveData.observe(this, Observer{ result ->
            val places = result.getOrNull()
            if (places != null) {
                recyclerView.visibility = View.VISIBLE
                bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }

            //对PlaceViewModel中的placeLiveData对象进行
            //观察，当有任何数据变化时，就会回调到传入的Observer接口实现中。然后我们会对回调的数
            //据进行判断：如果数据不为空，那么就将这些数据添加到PlaceViewModel的placeList集合
            //中，并通知PlaceAdapter刷新界面；如果数据为空，则说明发生了异常，此时弹出一个Toast
            //提示，并将具体的异常原因打印出来。
           })
    }
}