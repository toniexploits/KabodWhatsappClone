package com.charlesadebolaministries.whatsappclone.util

import android.os.Parcel
import android.os.Parcelable

data class User(
    val email: String? = "",
    val name: String? = "",
    val phone: String? = "",
    val country: String? = "",
    val status: String? = "",
    val birthDate: String? = "",
    val maritalStatus: String? = "",
    val occupation: String? = "",
    val cityState: String? = "",
    val imageUrl: String? = "",
    val statusUrl: String? = "",
    val statusTime: String? = ""
)

data class Contact(
    val name: String?,
    val phone: String?
)

data class Chat(
    val chatParticipants: ArrayList<String>
)

data class Message(
    val sendBy: String? = "",
    val message: String? = "",
    val messageTime: Long? = 0
)

data class AttachOption(
    val image: String?,
    val title: String?
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(image)
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AttachOption> {
        override fun createFromParcel(parcel: Parcel): AttachOption {
            return AttachOption(parcel)
        }

        override fun newArray(size: Int): Array<AttachOption?> {
            return arrayOfNulls(size)
        }
    }

}

data class StatusListElement(
    val userName: String?,
    val userUrl: String?,
    val status: String?,
    val statusUrl: String?,
    val statusTime: String?
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userName)
        parcel.writeString(userUrl)
        parcel.writeString(status)
        parcel.writeString(statusUrl)
        parcel.writeString(statusTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StatusListElement> {
        override fun createFromParcel(parcel: Parcel): StatusListElement {
            return StatusListElement(parcel)
        }

        override fun newArray(size: Int): Array<StatusListElement?> {
            return arrayOfNulls(size)
        }
    }

}