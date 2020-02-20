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

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    init {
        testeStreamData("teste")
    }

    fun testeStreamData(str: String){
        GlobalScope.launch {
            try {
                val listResult = ApiService().api!!.getTweetList(str).await()
                withContext(Dispatchers.Main) {
                    val reader = JsonReader(
                        InputStreamReader(listResult.byteStream())
                    )
                    val gson = GsonBuilder().create()

                    var j = gson.fromJson<JsonObject>(reader, JsonObject::class.java)
                    //val text = j.get("text").getAsString()

                    Log.d("debug", "JSON: " + j.toString())

                    var t = Tweet.fromJsonObject(j)

                    // https://stackoverflow.com/questions/47941537/notify-observer-when-item-is-added-to-list-of-livedata
                        tweetsList.add(t)
                        _tweetsList.value = tweetsList
                }
            }
            catch (e: Exception) {
                withContext(Dispatchers.IO) {
                    _status.value = DataApiStatus.ERROR
                }
            }
        }
    }

    fun searchStream(str: String) {
        Log.d("debug", "Search Parameter - $str")
        //getStreamData(str)
    }

    private fun getStreamData(track: String) {
        coroutineScope.launch {
            // Get the Deferred object for our Retrofit request
            var listResult = ApiService().api!!.getTweetList(track).await()
            //_status.value = DataApiStatus.LOADING

            try {
                _status.value = DataApiStatus.LOADING
                // this will run on a thread managed by Retrofit
                _status.value = DataApiStatus.DONE

                while (!listResult.source().exhausted()) {
                    //Log.d("debug", "Here: " + listResult.source().readUtf8Line())
                    val reader = JsonReader(
                        InputStreamReader(listResult.byteStream())
                    )
                    val gson = GsonBuilder().create()

                    var j = gson.fromJson<JsonObject>(reader, JsonObject::class.java)
                    //val text = j.get("text").getAsString()

                    Log.d("debug", "JSON: " + j.toString())

                    var t = Tweet.fromJsonObject(j)

                    // https://stackoverflow.com/questions/47941537/notify-observer-when-item-is-added-to-list-of-livedata
                    withContext(Dispatchers.Main){
                        tweetsList.add(t)
                        _tweetsList.value = tweetsList
                    }

                    Log.d("debug", "Tweets size ${tweetsList.size}")
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
            } catch (e: Exception) {
                Log.d("debug", "Error!! $e")
                withContext(Dispatchers.Main){
                    _status.value = DataApiStatus.ERROR
                    _tweetsList.value = ArrayList()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}