package com.example.firebaseexample.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firebaseexample.data.model.Note
import com.example.firebaseexample.data.repository.NoteRepository
import com.example.firebaseexample.util.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository
):ViewModel() {

    private val _notes = MutableLiveData<UIState<List<Note>>>()
    val notes: LiveData<UIState<List<Note>>>
        get() = _notes

    private val _addNote = MutableLiveData<UIState<String>>()
    val addNote: LiveData<UIState<String>>
        get() = _addNote
    fun getNotes() {
        _notes.value = UIState.Loading

        noteRepository.getNotes {
            _notes.value = it
        }
    }

    fun addNote(note: Note){
        _addNote.value = UIState.Loading
        noteRepository.addNote(note) {
            _addNote.value = it
        }
    }
}