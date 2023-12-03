package com.example.firebaseexample.data.repository

import android.net.Uri
import com.example.firebaseexample.data.model.Note
import com.example.firebaseexample.data.model.User
import com.example.firebaseexample.util.UIState

interface NoteRepository {
    fun getNotes(user: User?, result: (UIState<List<Note>>)-> Unit)
    fun addNote(note: Note, result: (UIState<Pair<Note,String>>) -> Unit)
    fun updateNote(note: Note, result: (UIState<String>)-> Unit)
    fun deleteNote(note: Note, result: (UIState<String>)-> Unit)
    suspend fun uploadSingleFile(fileUri: Uri, onResult: (UIState<Uri>) -> Unit)
    suspend fun uploadMultipleFile(fileUri: List<Uri>, onResult: (UIState<List<Uri>>) -> Unit)
}