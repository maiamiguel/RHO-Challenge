package com.ua.rho_challenge.viewmodels

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import com.ua.rho_challenge.utils.TTLList
import com.ua.rho_challenge.models.Tweet
import com.ua.rho_challenge.models.User
import com.ua.rho_challenge.network.network.ApiService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStreamReader
import com.ua.rho_challenge.ui.OverviewFragment

enum class DataApiStatus { LOADING, ERROR, DONE, NO_CONNECTION }

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel(private val state: SavedStateHandle) : ViewModel() {
    // Internally, we use a MutableLiveData, because we will be updating the List of Tweets with new values
    // The external LiveData interface to the property is immutable, so only this class can modify
    val properties: LiveData<TTLList<Tweet>>
        get() = _tweetsList

    private val tweetsList = TTLList<Tweet>()

    // The internal MutableLiveData that stores the list of tweets
    private val _tweetsList = MutableLiveData<TTLList<Tweet>>()

    // The internal MutableLiveData that stores the status of the request
    private val _status = MutableLiveData<DataApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<DataApiStatus>
        get() = _status

    private var currentCall: Call<ResponseBody>? = null

    // Just for testing
    fun insertNewTweet() {
        tweetsList.add(
            Tweet(
                "teste",
                "teste",
                "teste",
                User(
                    "https://firebasestorage.googleapis.com/v0/b/spicadiary-32494.appspot.com/o/TOUTBd65z4gSkEvaxJkPLL3H6782%2F12-12-2019%2000-20-27%2FPhotos%2F12-12-2019%2000-20-27_0.jpg?alt=media&token=234c5832-a6b2-499c-83df-fc9875621ffe",
                    "teste"
                )
            )
        )
        _tweetsList.value = tweetsList
    }

    fun searchStream(str: String) {
        Log.d("debug", "Search parameter to stream - $str")
        currentCall = ApiService().api!!.getTweetList(str);
        currentCall?.enqueue(streamResponse);
        Handler(Looper.getMainLooper()).post {
            _status.value = DataApiStatus.LOADING
        }
    }

    private val streamResponse: Callback<ResponseBody> = object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            Log.d("debug", "Getting data - ON RESPONSE")
            if (response.isSuccessful) {
                Handler(Looper.getMainLooper()).post {
                    _status.value = DataApiStatus.DONE
                }
                Thread(Runnable {
                    try {
                        val reader = JsonReader(InputStreamReader(response.body()!!.byteStream()))
                        val gson = GsonBuilder().create()

                        while (true) {
                            val j = gson.fromJson<JsonObject>(reader, JsonObject::class.java)

                            Log.d("debug", "JSON: $j")

                            if (j.getAsJsonObject("user") != null) {
                                val tweet = gson.fromJson(j, Tweet::class.java)
                                tweetsList.add(tweet)
                                updateUI(tweetsList)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("error", "ERROR : ${e.message}")
                    }
                }).start()
            }
        }

        override fun onFailure(call: Call<ResponseBody?>?, t: Throwable?) {
            Log.e("error", "onFailure call")
            Handler(Looper.getMainLooper()).post {
                _status.value = DataApiStatus.ERROR
            }
        }
    }

    private fun updateUI(tweetsList : TTLList<Tweet>){
        Handler(Looper.getMainLooper()).post {
            Log.d("debug", "Updating UI from UI thread")
            _tweetsList.value = tweetsList
        }
    }

    override fun onCleared() {
        Log.d("debug", "onCleared")
        super.onCleared()
        currentCall?.cancel()
    }

    fun isJobRunning(): Boolean {
        if (currentCall != null){
            return currentCall!!.isExecuted
        }
        return false
    }

    fun cancelJob() {
        Log.d("debug", "Cancelling current Job!")
        if (currentCall != null){
            currentCall!!.cancel()
        }
    }
}