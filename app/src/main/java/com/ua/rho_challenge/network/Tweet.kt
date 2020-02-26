package com.ua.rho_challenge.network

import com.google.gson.annotations.SerializedName

/**
 * Class regarding tweet data
 */
class Tweet(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id_str")
    val idStr: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("user")
    val user: User
){

}