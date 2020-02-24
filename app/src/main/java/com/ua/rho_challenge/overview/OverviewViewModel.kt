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
    // Internally, we use a MutableLiveData, because we will be updating the List of Tweets with new values
    // The external LiveData interface to the property is immutable, so only this class can modify

    val properties: LiveData<ArrayList<Tweet>>
        get() = _tweetsList

    private val tweetsList = ArrayList<Tweet>()
    // The internal MutableLiveData that stores the list of tweets
    private val _tweetsList = MutableLiveData<ArrayList<Tweet>>()

    // The internal MutableLiveData that stores the status of the request
    private val _status = MutableLiveData<DataApiStatus>()
    // The external immutable LiveData for the request status
    val status: LiveData<DataApiStatus>
        get() = _status

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Default)

    fun getStreamData(str: String) {
        coroutineScope.launch {
            withContext(Dispatchers.Main) {
                //Display loading animation in UI
                _status.value = DataApiStatus.LOADING
            }
            try {
                val listResult = ApiService().api!!.getTweetList(str).await()

                while (!listResult.source().exhausted()) {
                    val reader = JsonReader( InputStreamReader(listResult.byteStream()) )
                    val gson = GsonBuilder().create()
                    val j = gson.fromJson<JsonObject>(reader, JsonObject::class.java)
                    Log.d("debug", "JSON: " + j.toString())

                    val t = Tweet.fromJsonObject(j)

                    withContext(Dispatchers.Main) {
                        _status.value = DataApiStatus.DONE
                        // https://stackoverflow.com/questions/47941537/notify-observer-when-item-is-added-to-list-of-livedata
                        tweetsList.add(t)
                        _tweetsList.value = tweetsList
                    }
                }
            } catch (e: Exception) {
                Log.e("error", "ERROR ${e.message}")
            }
        }
    }

    fun searchStream(str: String) {
        Log.d("debug", "Search parameter to stream - $str")
        getStreamData(str)
    }

    fun unavailableInternetConnection(){
        _status.value = DataApiStatus.ERROR
    }

    override fun onCleared() {
        Log.d("debug", "onCleared")
        super.onCleared()
        viewModelJob.cancel()
    }
}