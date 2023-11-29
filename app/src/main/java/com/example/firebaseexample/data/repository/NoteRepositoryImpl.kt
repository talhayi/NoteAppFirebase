package com.example.firebaseexample.data.repository

import com.example.firebaseexample.data.model.Note
import com.example.firebaseexample.util.UIState
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class NoteRepositoryImpl(
    val database: FirebaseFirestore
): NoteRepository {
    override fun getNotes(): UIState<List<Note>> {
        val data = arrayListOf(
            Note(
                id = "1",
                text = "Note",
                date = Date(),
            )
        )
        return if (data.isNullOrEmpty()){
            UIState.Failure("Data is empty")
        }else{
            UIState.Success(data)
        }
    }
}