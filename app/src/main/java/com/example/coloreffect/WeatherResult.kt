package com.example.coloreffect

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import com.example.coloreffect.WeatherResultFragment
import java.util.concurrent.TimeUnit

class WeatherResult : AppCompatActivity(), WeatherResultFragment.ForTransferResult {

    lateinit var workManager: WorkManager
    lateinit var myWorkRequest: OneTimeWorkRequest
    lateinit var resultFragment:WeatherResultFragment
    lateinit var myData:Data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate savedInstanceState$savedInstanceState")
        setContentView(R.layout.activity_weather_result)
        resultFragment =
            WeatherResultFragment.newInstance(intent.getSerializableExtra(WeatherResultFragment.DATA_FOR_BUNDLE))
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, resultFragment)
        transaction.commit()

        val dataForBundle =
            intent.getSerializableExtra(WeatherResultFragment.DATA_FOR_BUNDLE) as DataForBundle?
        var pressure = false
        var feels = false
        var humidity = false
        if (dataForBundle?.resultPressure != null) {
            pressure = true
        }
        if (dataForBundle?.resultFeels != null) {
            feels = true
        }
        if (dataForBundle?.resultHumidity != null) {
            humidity = true
        }
        myData = Data.Builder()
            .putString("City", dataForBundle?.city)
            .putBoolean("Pressure", pressure)
            .putBoolean("Feels", feels)
            .putBoolean("Humidity", humidity)
            .build()
//        myWorkRequest =
//            PeriodicWorkRequestBuilder<LoadDataService>(15, TimeUnit.MINUTES, 14, TimeUnit.MINUTES)
//                .setInputData(myData)
//                .addTag("love")
//                .build()

//        myWorkRequest =
//            OneTimeWorkRequestBuilder<LoadDataService>()
//                .setInputData(myData)
//                .addTag("love")
//                .build()
//        workManager = WorkManager.getInstance(applicationContext)
//        workManager.enqueue(myWorkRequest)


    }

    fun dataToBundle(data: Data): DataForBundle? {
        val bundle = DataForBundle(data.getString("resultPressure"),
            data.getString("resultFeels"),
            data.getString("resultHumidity"),
            data.getString("resultWeather"),
            data.getString("dateForHistory"),
            data.getString("resultWeatherHistory"),
            data.getString("iconCode"),
            data.getString("cityForBundle"))
        return bundle

    }

    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    companion object {

        private const val TAG = "### WeatherResult"
    }

    override fun transferResult() {
        myWorkRequest =
            OneTimeWorkRequestBuilder<LoadDataService>()
                .setInputData(myData)
                .addTag("love")
                .build()
        workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueue(myWorkRequest)
        var resultWeather: Data? = null
        workManager.getWorkInfoByIdLiveData(myWorkRequest.id)
            .observe(this, Observer { info ->
                Log.d("tranferStatus", info.state.toString())
                Log.d("tranfer5", info.outputData.toString())
                if (info != null && info.state == WorkInfo.State.SUCCEEDED) {
                    resultWeather = info.outputData
                    Log.d("tranfer5 повтор", info.outputData.toString())
                    Log.d("ПОВТОР resultWeather", resultWeather.toString())
                    if (resultWeather != null) {
                        Log.d("дата бандл", resultFragment.dataForBundle.toString())
                        resultFragment.dataForBundle= dataToBundle(resultWeather!!)
                        resultFragment.dataExtraction()
                        Log.d("дата бандл после", resultFragment.dataForBundle.toString())
                        workManager.cancelWorkById(myWorkRequest.id)
                    }
                }
                Log.d("отменил?", info.state.toString())
            })
        workManager.getWorkInfosByTagLiveData("love")
            .observe(this, { info ->
                Log.d("tranfer3", info[0].outputData.toString())
            })
//        workManager.cancelWorkById(myWorkRequest.id)
//        workManager.cancelAllWorkByTag("love")
//        if (resultWeather != null) {
//            Log.d("дата бандл", resultFragment.dataForBundle.toString())
//            resultFragment.dataForBundle= dataToBundle(resultWeather!!)
//            resultFragment.dataExtraction()
//            Log.d("дата бандл после", resultFragment.dataForBundle.toString())
//        }

//        Log.d("по итогу", resultWeather?.getString("resultWeather")!!)
        Log.d("по итогу", resultWeather.toString())
    }
}