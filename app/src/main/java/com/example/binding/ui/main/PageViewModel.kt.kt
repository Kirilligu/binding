package com.example.binding.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.binding.WeatherApi
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL

class PageViewModel : ViewModel() {
    private val _index = MutableLiveData<Int>()
    private val _weatherData = MutableLiveData<WeatherApi>()
    private val _loading = MutableLiveData<Boolean>().apply { value = false }
    val temp: LiveData<String> = _weatherData.map {
        it?.main?.temp?.toString() ?: "Loading"
    }
    val wind: LiveData<String> = _weatherData.map {
        it?.wind?.speed?.toString() ?: "Loading"
    }
    val weather: LiveData<String> = _weatherData.map {
        it?.weather?.get(0)?.main ?: "Loading"
    }
    fun setIndex(index: Int) {
        _index.value = index
        _loading.value = true
        GlobalScope.launch(Dispatchers.IO) {
            loadWeather(if (index == 1) "Irkutsk" else "Novosibirsk")
        }
    }
    private suspend fun loadWeather(city: String) {
        val API_KEY = "4dac63af9f28277443bf2d581d6cc50b"
        val weatherURL = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$API_KEY&units=metric"
        try {
            val stream = URL(weatherURL).getContent() as InputStream
            val gson = Gson()
            _weatherData.postValue(gson.fromJson(InputStreamReader(stream), WeatherApi::class.java))
        } catch (e: Exception) {
            Log.e("PageViewModel", "Error loading weather data", e)
        } finally {
            _loading.postValue(false)
        }
    }
}