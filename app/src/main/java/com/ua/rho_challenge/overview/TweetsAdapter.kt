package com.ua.rho_challenge.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ua.rho_challenge.R
import com.ua.rho_challenge.databinding.TweetViewItemBinding
import com.ua.rho_challenge.network.Tweet

/**
 * This class implements a [RecyclerView] [ListAdapter] which uses Data Binding to present [List] data
 */
class TweetsAdapter : RecyclerView.Adapter<TweetsAdapter.TweetViewHolder?>() {

    private var tweets: ArrayList<Tweet> = ArrayList()

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): TweetViewHolder {
        val tweetListItemBinding: TweetViewItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context),
            R.layout.tweet_view_item, viewGroup, false
        )
        return TweetViewHolder(tweetListItemBinding)
    }

    override fun onBindViewHolder(
        tweetViewHolder: TweetViewHolder,
        i: Int
    ) {
        val currentStudent: Tweet = tweets.get(i)
        tweetViewHolder.tweetsListItemBinding.property = currentStudent
    }

    override fun getItemCount(): Int {
        return if (tweets.isEmpty()) 0 else tweets.size
    }

    fun setTweetsList(tweets: ArrayList<Tweet>) {
        this.tweets = tweets
        notifyDataSetChanged()
    }

    inner class TweetViewHolder(tweetListItemBinding: TweetViewItemBinding) : RecyclerView.ViewHolder(tweetListItemBinding.getRoot()) {
        internal val tweetsListItemBinding: TweetViewItemBinding = tweetListItemBinding
    }
}