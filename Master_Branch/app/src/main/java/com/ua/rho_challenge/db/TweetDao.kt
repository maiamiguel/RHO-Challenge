package com.ua.rho_challenge.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ua.rho_challenge.models.Tweet
import com.ua.rho_challenge.utils.TTLList

@Dao
interface TweetDao {
    @Query("SELECT * from tweet_table")
    fun getTweets(): LiveData<List<Tweet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(t: Tweet)

    @Query("DELETE FROM tweet_table")
    suspend fun deleteAll()
}