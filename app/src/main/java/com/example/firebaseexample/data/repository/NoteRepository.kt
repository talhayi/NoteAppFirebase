package com.example.firebaseexample.data.repository

import com.example.firebaseexample.data.model.Note
import com.example.firebaseexample.util.UIState

interface NoteRepository {
    fun getNotes(): UIState<List<Note>>
}