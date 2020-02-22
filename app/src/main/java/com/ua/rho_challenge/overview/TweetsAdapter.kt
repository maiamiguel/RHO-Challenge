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

    private var tweets: ArrayList<Tweet>? = null

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
        employeeViewHolder: TweetViewHolder,
        i: Int
    ) {
        val currentStudent: Tweet = tweets!!.get(i)
        employeeViewHolder.tweetsListItemBinding.property = currentStudent
    }

    override fun getItemCount(): Int {
        return if (tweets != null) {
            tweets!!.size
        } else {
            0
        }
    }

    fun setEmployeeList(employees: ArrayList<Tweet>) {
        this.tweets = employees
        notifyDataSetChanged()
    }

    inner class TweetViewHolder(tweetListItemBinding: TweetViewItemBinding) : RecyclerView.ViewHolder(tweetListItemBinding.getRoot()) {
        internal val tweetsListItemBinding: TweetViewItemBinding = tweetListItemBinding
    }
}