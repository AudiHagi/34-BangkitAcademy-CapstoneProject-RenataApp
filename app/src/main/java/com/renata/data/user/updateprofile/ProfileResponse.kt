package com.renata.data.user.updateprofile

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("data")
    val data: DataProfile
)

data class DataProfile(
    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("full_name")
    val fullName: String,

    @field:SerializedName("first_name")
    val firstName: String,

    @field:SerializedName("last_name")
    val lastName: String,

    @field:SerializedName("phone")
    val phone: String,

    @field:SerializedName("address")
    val address: String,

    @field:SerializedName("avatar_link")
    val avatarLink: String
)


