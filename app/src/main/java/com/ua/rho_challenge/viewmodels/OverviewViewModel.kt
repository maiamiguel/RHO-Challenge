package com.ua.rho_challenge.viewmodels

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import com.ua.rho_challenge.db.AppDatabase
import com.ua.rho_challenge.db.TweetDao
import com.ua.rho_challenge.models.Tweet
import com.ua.rho_challenge.models.User
import com.ua.rho_challenge.network.network.ApiService
import com.ua.rho_challenge.ui.OverviewFragment
import com.ua.rho_challenge.utils.TTLList
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStreamReader

enum class DataApiStatus { LOADING, ERROR, DONE, NO_CONNECTION }

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel(application: Application) : AndroidViewModel(application) {
    // Internally, we use a MutableLiveData, because we will be updating the List of Tweets with new values
    // The external LiveData interface to the property is immutable, so only this class can modify
    val properties: LiveData<TTLList<Tweet>>
        get() = _tweetsList

    // The internal MutableLiveData that stores the list of tweets
    private val _tweetsList = MutableLiveData<TTLList<Tweet>>()

    private var tweetsList = TTLList<Tweet>()

    // The internal MutableLiveData that stores the status of the request
    private val _status = MutableLiveData<DataApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<DataApiStatus>
        get() = _status

    private var currentCall: Call<ResponseBody>? = null


    private val tweetsDao: TweetDao = AppDatabase.getDatabase(application).tweetDao()
    val tweetsDB : LiveData<List<Tweet>> = tweetsDao.getTweets()

    init {
        Log.d("debug", "SIZE ${tweetsDB.value?.size.toString()}")
    }

    fun searchStream(str: String) {
        // If a new search is done, the previous query info must be substituted
        viewModelScope.launch {
            tweetsDao.deleteAll()
        }

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

                                // Persistence. Inserting Tweet in DB so if there isn't a connection, previous tweets are shown
                                viewModelScope.launch {
                                    insertTweetDB(tweet)
                                }

                                tweetsList.add(tweet)
                                // Updating UI
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

    private fun updateUI(tweetsList: TTLList<Tweet>) {
        Handler(Looper.getMainLooper()).post {
            Log.d("debug", "Updating UI from Main thread")
            _tweetsList.value = tweetsList
        }
    }

    override fun onCleared() {
        Log.d("debug", "onCleared")
        super.onCleared()
        currentCall?.cancel()
    }

    fun isJobRunning(): Boolean {
        if (currentCall != null) {
            return currentCall!!.isExecuted
        }
        return false
    }

    fun cancelJob() {
        Log.d("debug", "Cancelling current Job!")
        if (currentCall != null) {
            currentCall!!.cancel()
        }
    }

    suspend fun insertTweetDB(t: Tweet) {
        Log.d("debug", "Inserting Tweet into DB $t")
        tweetsDao.insert(t)
    }
}