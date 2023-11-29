package com.example.firebaseexample.data.repository

import com.example.firebaseexample.data.model.Note

interface NoteRepository {
    fun getNotes(): List<Note>
}