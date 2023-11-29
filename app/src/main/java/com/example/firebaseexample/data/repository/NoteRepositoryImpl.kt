package com.example.firebaseexample.data.repository

import com.example.firebaseexample.data.model.Note
import com.google.firebase.firestore.FirebaseFirestore

class NoteRepositoryImpl(
    val database: FirebaseFirestore
): NoteRepository {
    override fun getNotes(): List<Note> {
        return arrayListOf()
    }
}