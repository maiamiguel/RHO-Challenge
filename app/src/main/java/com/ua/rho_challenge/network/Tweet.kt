package com.ua.rho_challenge.network

import com.google.gson.JsonObject

class Tweet(
    val createdAt: String,
    val idStr: String,
    val text: String,
    val profileImgUrl: String,
    val userName: String
) {
    companion object {
        fun fromJsonObject(jsonObject: JsonObject): Tweet {
            return Tweet(
                jsonObject["created_at"].asString,
                jsonObject["id_str"].asString,
                jsonObject["text"].asString,
                jsonObject.getAsJsonObject("user").get("profile_image_url_https").getAsString(),
                jsonObject.getAsJsonObject("user").get("name").getAsString()
            )
        }
    }
}