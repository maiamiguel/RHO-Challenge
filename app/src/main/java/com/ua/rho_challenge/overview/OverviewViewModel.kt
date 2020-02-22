package com.ua.rho_challenge.overview

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import com.ua.rho_challenge.network.Tweet
import com.ua.rho_challenge.network.network.ApiService
import kotlinx.coroutines.*
import java.io.InputStreamReader

enum class DataApiStatus { LOADING, ERROR, DONE }

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel : ViewModel() {
    // Internally, we use a MutableLiveData, because we will be updating the List of MarsProperty with new values
    // The external LiveData interface to the property is immutable, so only this class can modify
    val properties: LiveData<ArrayList<Tweet>>
        get() = _tweetsList

    private val tweetsList = ArrayList<Tweet>()
    private val _tweetsList = MutableLiveData<ArrayList<Tweet>>()

    // The internal MutableLiveData that stores the status of the request
    private val _status = MutableLiveData<DataApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<DataApiStatus>
        get() = _status

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()
    
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    init {
        //getStreamData("teste")
    }

    fun getStreamData(str: String) {
        coroutineScope.launch {
            //            withContext(Dispatchers.Main) {
//                _status.value = DataApiStatus.LOADING
//            }
            try {
                val listResult = ApiService().api!!.getTweetList(str).await()

                //while (!listResult.source().exhausted()) {
                    Log.d("debug", listResult.byteStream().toString())
                    //_status.value = DataApiStatus.LOADING
                    val reader = JsonReader(
                        InputStreamReader(listResult.byteStream())
                    )
                    val gson = GsonBuilder().create()

                    val j = gson.fromJson<JsonObject>(reader, JsonObject::class.java)
                    //val text = j.get("text").getAsString()

                    Log.d("debug", "JSON: " + j.toString())
                    //while (true) {
                    val t = Tweet.fromJsonObject(j)

                    withContext(Dispatchers.Main) {
                        //_status.value = DataApiStatus.DONE
                        // https://stackoverflow.com/questions/47941537/notify-observer-when-item-is-added-to-list-of-livedata
                        tweetsList.add(t)
                        //tweetsList.add(t)
                        _tweetsList.value = tweetsList
                        // }
                    }

                    Log.d("debug", "END")
                //}
            } catch (e: Exception) {
                Log.d("debug", "ERROR ${e.message}")
                withContext(Dispatchers.Main) {
                    _status.value = DataApiStatus.ERROR
                }
            }
        }
    }

    fun searchStream(str: String) {
        Log.d("debug", "Search Parameter - $str")
        //getStreamData(str)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

//                twitterCall.subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(
//                        { result ->
//                            while (!result.source().exhausted()) {
//                                //Log.d("debug", "Here: " + result.source().readUtf8Line())
//                                val reader = JsonReader(
//                                    InputStreamReader(result.byteStream())
//                                )
//                                val gson = GsonBuilder().create()
//
//                                var j = gson.fromJson<JsonObject>(reader, JsonObject::class.java)
//                                //val text = j.get("text").getAsString()
//
//                                //Log.d("debug", "JSON: " + j.toString())
//
//                                var t = Tweet.fromJsonObject(j)
//
//                                _text.value = t.userName
//
//                                // https://stackoverflow.com/questions/47941537/notify-observer-when-item-is-added-to-list-of-livedata
//                                tweetsList.add(t)
//                                _tweetsList.value = tweetsList
//
//                                Log.d("debug", "Tweets size ${tweetsList.size}")
//                            }
//                        },
//                        { error -> Log.e("ERROR", error.message) }
//                    )