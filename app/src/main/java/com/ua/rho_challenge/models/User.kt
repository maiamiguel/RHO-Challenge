package com.ua.rho_challenge.models

import com.google.gson.annotations.SerializedName

class User(
    @SerializedName("profile_image_url_https")
    val profilePic: String,
    @SerializedName("name")
    val username: String
){

}
