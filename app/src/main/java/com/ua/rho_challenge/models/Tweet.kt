package com.ua.rho_challenge.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Class regarding tweet data
 */
@Entity(tableName = "tweet_table")
class Tweet(
    @PrimaryKey @ColumnInfo(name = "createdAt")
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id_str")
    val idStr: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("user")
    @Embedded val user: User
){

}