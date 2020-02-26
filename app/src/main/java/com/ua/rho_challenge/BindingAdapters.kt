package com.ua.rho_challenge

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ua.rho_challenge.overview.DataApiStatus

/**
 * Uses the Glide library to load an image by URL into an [ImageView]
 */
@BindingAdapter("profileImageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image))
            .into(imgView)
    }
}

/**
 * This binding adapter displays the [DataApiStatus] of the network request in an image view.  When
 * the request is loading, it displays a loading_animation.  If the request has an error, it
 * displays a broken image to reflect the connection error.  When the request is finished, it
 * hides the image view.
 */
@BindingAdapter("apiStatusRecycler")
fun bindStatusRecycler(rv: RecyclerView, status: DataApiStatus?) {
    when (status) {
        DataApiStatus.LOADING -> {
            rv.visibility = View.INVISIBLE
        }
        DataApiStatus.ERROR -> {
            rv.visibility = View.GONE
        }
        DataApiStatus.DONE -> {
            rv.visibility = View.VISIBLE
        }
    }
}

/**
 * This binding adapter displays the [DataApiStatus] of the network request in an image view.  When
 * the request is loading, it displays a loading_animation.  If the request has an error, it
 * displays a broken image to reflect the connection error.  When the request is finished, it
 * hides the image view.
 */
@BindingAdapter("apiStatus")
fun bindStatus(statusImageView: ImageView, status: DataApiStatus?) {
    when (status) {
        DataApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        DataApiStatus.NO_CONNECTION -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        DataApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.error)
        }
        DataApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}

@BindingAdapter("errorTV")
fun bindErrorTextView(tv: TextView, status: DataApiStatus?) {
    when (status) {
        DataApiStatus.LOADING -> {
            tv.visibility = View.GONE
        }
        DataApiStatus.ERROR -> {
            tv.visibility = View.VISIBLE
        }
        DataApiStatus.DONE -> {
            tv.visibility = View.GONE
        }
        DataApiStatus.NO_CONNECTION -> {
            tv.visibility = View.GONE
        }
    }
}

@BindingAdapter("dateFormatted")
fun transformDate(tv : TextView, data : String) {
    val splited = data.split(" ")
    val s = "${splited[3]} - ${splited[2]} ${splited[1]} ${splited[5]}"
    tv.text = s
}