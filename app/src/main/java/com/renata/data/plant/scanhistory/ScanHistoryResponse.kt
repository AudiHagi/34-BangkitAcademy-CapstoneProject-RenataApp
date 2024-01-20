package com.renata.data.plant.scanhistory

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class ScanHistoryResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val dataHistory: DataHistory
) : Parcelable

@Parcelize
data class DataHistory(
    @SerializedName("user_id")
    val id: String,

    @SerializedName("scanHistories")
    val scanHistory: List<ScanHistory>
) : Parcelable

@Parcelize
data class ScanHistory(
    @SerializedName("scan_id")
    val scanId: String,

    @SerializedName("soil_type")
    val soilType: String,

    @SerializedName("date_scan")
    val date: String,

    @SerializedName("soil_image")
    val image: String
) : Parcelable