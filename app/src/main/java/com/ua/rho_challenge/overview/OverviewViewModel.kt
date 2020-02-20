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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.InputStreamReader

enum class DataApiStatus { LOADING, ERROR, DONE }
/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel : ViewModel() {
    // Internally, we use a MutableLiveData, because we will be updating the List of MarsProperty
    // with new values

    // The external LiveData interface to the property is immutable, so only this class can modify
    val properties: LiveData<List<Tweet>>
        get() = _tweetsList

    private val tweetsList = ArrayList<Tweet>()
    private val _tweetsList = MutableLiveData<List<Tweet>>()

    // The internal MutableLiveData that stores the status of the request
    private val _status = MutableLiveData<DataApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<DataApiStatus>
        get() = _status

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * Call getStreamData() on init so data can  be displayed ASAP.
     */
    init {
        getStreamData()
    }

    private fun getStreamData() {
        coroutineScope.launch {
            // Get the Deferred object for our Retrofit request
            var twitterCall = ApiService().api!!.getTweetList("teste")
            _status.value = DataApiStatus.LOADING

            try {
                // this will run on a thread managed by Retrofit
                var listResult = twitterCall.await()

//                while (!listResult.source().exhausted()){
//                    Log.d("debug", "Here: " + listResult.source().readUtf8Line())
//                }

                val reader = JsonReader(
                    InputStreamReader(listResult.byteStream())
                )
                val gson = GsonBuilder().create()

                var j = gson.fromJson<JsonObject>(reader, JsonObject::class.java)
                //val text = j.get("text").getAsString()

                //_text.value = text
                Log.d("debug", j.toString())

                var t = Tweet.fromJsonObject(j)

                // https://stackoverflow.com/questions/47941537/notify-observer-when-item-is-added-to-list-of-livedata
                tweetsList.add(t)
                _tweetsList.value = tweetsList

                Log.d("debug", tweetsList.toString())
                _status.value = DataApiStatus.DONE

            } catch (e: Exception) {
                Log.d("debug", "Error!! $e")
                _status.value = DataApiStatus.ERROR
                _tweetsList.value = ArrayList()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}