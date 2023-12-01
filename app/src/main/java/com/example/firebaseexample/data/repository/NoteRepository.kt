package com.example.firebaseexample.data.repository

import com.example.firebaseexample.data.model.Note
import com.example.firebaseexample.util.UIState

interface NoteRepository {
    fun getNotes(result: (UIState<List<Note>>)-> Unit)
    fun addNote(note: Note, result: (UIState<Pair<Note,String>>) -> Unit)
    fun updateNote(note: Note, result: (UIState<String>)-> Unit)
    fun deleteNote(note: Note, result: (UIState<String>)-> Unit)
}