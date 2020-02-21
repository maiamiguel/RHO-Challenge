package com.ua.rho_challenge.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ua.rho_challenge.databinding.GridViewItemBinding
import com.ua.rho_challenge.network.Tweet

/**
 * This class implements a [RecyclerView] [ListAdapter] which uses Data Binding to present [List]
 * data, including computing diffs between lists.
 * @param onClick a lambda that takes the
 */
class TweetsAdapter : ListAdapter<Tweet, TweetsAdapter.TweetsViewHolder>(DiffCallback) {

    /**
     * The TweetsViewHolder constructor takes the binding variable from the associated
     * GridViewItem, which nicely gives it access to the full [Tweet] information.
     */
    class TweetsViewHolder(private var binding: GridViewItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tweetProperties: Tweet) {
            binding.property = tweetProperties
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [Tweet]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<Tweet>() {
        override fun areItemsTheSame(oldItem: Tweet, newItem: Tweet): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Tweet, newItem: Tweet): Boolean {
            return oldItem.text.equals(newItem.text)
        }
    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetsViewHolder {
        return TweetsViewHolder(GridViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: TweetsViewHolder, position: Int) {
        val tweetProperties = getItem(position)
        holder.bind(tweetProperties)
    }
}
