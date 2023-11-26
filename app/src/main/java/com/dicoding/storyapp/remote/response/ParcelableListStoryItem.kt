package com.dicoding.storyapp.remote.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ParcelableListStoryItem(
    @SerializedName("photoUrl")
    val photoUrl: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("lon")
    val lon: Double? = null,

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("lat")
    val lat: Double? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(photoUrl)
        parcel.writeString(createdAt)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeValue(lon)
        parcel.writeString(id)
        parcel.writeValue(lat)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParcelableListStoryItem> {
        override fun createFromParcel(parcel: Parcel): ParcelableListStoryItem {
            return ParcelableListStoryItem(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableListStoryItem?> {
            return arrayOfNulls(size)
        }
    }
}
