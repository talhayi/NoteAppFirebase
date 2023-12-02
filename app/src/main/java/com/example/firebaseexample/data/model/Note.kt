package com.example.firebaseexample.data.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date


@Parcelize
data class Note(
    var id: String? = null,
    var userId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val tags: MutableList<String> = arrayListOf(),
    @ServerTimestamp
    val date: Date = Date(),
) : Parcelable
