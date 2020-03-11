package com.ua.rho_challenge.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.JsonReader
import com.ua.rho_challenge.models.Tweet
import com.ua.rho_challenge.models.User
import com.ua.rho_challenge.network.expiring_time
import com.ua.rho_challenge.network.network.ApiService
import kotlinx.coroutines.*
import java.io.InputStreamReader

enum class DataApiStatus { LOADING, ERROR, DONE, NO_CONNECTION }

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

    // Runs on the Dispatchers.Default due to JSON parsing. Cannot run on the Dispatchers.MAIN in order not to freeze the UI.
    private var coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Default)

    private var searchJob : Job? = null

    private suspend fun getStreamData(str: String) {
        Log.d("debug", "Fetching data..")

        withContext(Dispatchers.Main) {
            //Display loading animation in UI
            _status.value = DataApiStatus.LOADING
        }
        try {
            val listResult = ApiService().api!!.getTweetList(str).await()

            while (true) {
                val reader = JsonReader(InputStreamReader(listResult.byteStream()))
                // https://stackoverflow.com/questions/11484353/gson-throws-malformedjsonexception
                reader.isLenient = true;
                val gson = GsonBuilder().create()
                val j = gson.fromJson<JsonObject>(reader, JsonObject::class.java)

                Log.d("debug", "JSON: $j")

                if (j.get("text") != null && j.getAsJsonObject("user").get("profile_image_url_https") != null && j.getAsJsonObject("user").get("name") != null){
                    val t = gson.fromJson(j, Tweet::class.java)

                    withContext(Dispatchers.Main) {
                        _status.value = DataApiStatus.DONE
                        // https://stackoverflow.com/questions/47941537/notify-observer-when-item-is-added-to-list-of-livedata
                        tweetsList.add(t)
                        _tweetsList.value = tweetsList
                        ttlRemoval()
                    }
                }
            }
        }
        catch (e : JsonSyntaxException) {
            Log.e("error", "JsonSyntaxException ${e.message}");
        }
        catch (e: Exception) {
            Log.e("error", "ERROR ${e.message}")
        }
    }

    // Just for testing
    fun insertNewTweet(){
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

    private fun ttlRemoval() {
        viewModelScope.launch(Dispatchers.Default) {
            Log.d("debug", "Removing elements..")
            delay(expiring_time)
            Log.d("debug", "Removing elements - " + tweetsList.size)

            withContext(Dispatchers.Main) {
                _tweetsList.value = tweetsList
                tweetsList.remove(tweetsList.first())
            }
            Log.d("debug", "Removed elements - " + tweetsList.size)
        }
    }

    fun searchStream(str: String) {
        Log.d("debug", "Search parameter to stream - $str")
        searchJob = viewModelScope.launch(Dispatchers.Default){
            getStreamData(str)
        }
    }

    override fun onCleared() {
        Log.d("debug", "onCleared")
        super.onCleared()
        viewModelJob.cancel()
    }

    fun isJobRunning() : Boolean{
        if (searchJob != null){
            return searchJob?.isActive!!
        }
        return false
    }

    fun cancelJob(){
        Log.d("debug", "Cancelling current Job!")
        searchJob?.cancel()
    }
}
