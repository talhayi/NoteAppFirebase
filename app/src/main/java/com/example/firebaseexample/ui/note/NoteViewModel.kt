package com.example.firebaseexample.ui.note

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

    private val _addNote = MutableLiveData<UIState<Pair<Note,String>>>()
    val addNote: LiveData<UIState<Pair<Note,String>>>
        get() = _addNote

    private val _updateNote = MutableLiveData<UIState<String>>()
    val updateNote: LiveData<UIState<String>>
        get() = _updateNote

    private val _deleteNote = MutableLiveData<UIState<String>>()
    val deleteNote: LiveData<UIState<String>>
        get() = _deleteNote
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

    fun updateNote(note: Note){
        _updateNote.value = UIState.Loading
        noteRepository.updateNote(note) {
            _updateNote.value = it
        }
    }

    fun deleteNote(note: Note){
        _deleteNote.value = UIState.Loading
        noteRepository.deleteNote(note) {
            _deleteNote.value = it
        }
    }
}