package com.example.firebaseexample.data.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date


@Parcelize
data class Note(
    var id: String? = null,
    val text: String? = null,
    @ServerTimestamp
    val date: Date = Date(),
): Parcelable
