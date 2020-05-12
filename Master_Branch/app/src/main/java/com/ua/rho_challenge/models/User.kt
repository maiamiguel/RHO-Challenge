package com.ua.rho_challenge.models

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Class regarding user data
 */
class User(
    @SerializedName("profile_image_url_https")
    val profilePic: String,
    @SerializedName("name")
    @ColumnInfo(name = "username")
    val username: String
){

}
