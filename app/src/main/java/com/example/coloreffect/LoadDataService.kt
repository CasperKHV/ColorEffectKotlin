package com.example.coloreffect

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.*


class LoadDataService(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

//    class LoadDataService (
//        val context: Context,
//        var city: String?,
//        var pressure: Boolean,
//        var feels: Boolean,
//        var humidity: Boolean,
//        workerParameters: WorkerParameters
//    ) :
//        Worker(context, workerParameters) {


//    init {
//        city = inputData.getString("City")
//        pressure = inputData.getBoolean("Pressure", false)
//        feels = inputData.getBoolean("Feels", false)
//        humidity = inputData.getBoolean("Humidity", false)
//        Log.d("Service", "init is done")
//
//    }

    var context1: Context?
    var city: String?
    var pressure: Boolean = false
    var feels: Boolean = false
    var humidity: Boolean = false

    init {
        context1 = context
        city = inputData.getString("City")
        pressure = inputData.getBoolean("Pressure", false)
        feels = inputData.getBoolean("Feels", false)
        humidity = inputData.getBoolean("Humidity", false)
        Log.d("Service", "init is done")
    }

    val controller = Controller()
    var weather: ModelForGSONWeatherClass? = ModelForGSONWeatherClass();


    fun loadData(city: String?): Data {
        weather = controller.start(context1, city)
        val cityForBundle = weather!!.name
        val currentDate = Date()
        var resultPressure: String? = null
        var resultFeels: String? = null
        var resultHumidity: String? = null
        val resultWeather = WeatherSpec.getWeather(context1, weather!!)
        val resultWeatherHistory = WeatherSpec.getWeatherHistory(context1, weather!!, currentDate)
        val dateForHistory = WeatherSpec.getDate(context1, currentDate)
        if (pressure == true) {
            resultPressure = WeatherSpec.getPressure(context1, weather!!)
        }
        if (feels == true) {
            resultFeels = WeatherSpec.getFeels(context1, weather!!)
        }
        if (humidity == true) {
            resultHumidity = WeatherSpec.getHumidity(context1, weather!!)
        }
        val iconCode = weather!!.weather[0]!!.icon
        val output = Data.Builder()
            .putString("cityForBundle", cityForBundle)
            .putString("resultWeather", resultWeather)
            .putString("resultWeatherHistory", resultWeatherHistory)
            .putString("dateForHistory", dateForHistory)
            .putString("iconCode", iconCode)
            .putString("resultPressure", resultPressure)
            .putString("resultFeels", resultFeels)
            .putString("resultHumidity", resultHumidity)
            .build()
        Log.d("Service", cityForBundle+resultWeather+dateForHistory)
        Log.d("Service", "fun loadData is done")
        Log.d("Output", output.toString())
        return output
    }

    override fun doWork(): Result {
        val output: Data
        try {
            output = loadData(city)
            Log.d("Service", "fun doWork is trying")
        } catch (e: Exception) {
            return Result.failure()

        }
        Log.d("Service", "fun doWork is done")
        Log.d("Outputfinal", output.toString())
        return Result.success(output)
    }

}