package com.example.firebaseexample.ui.note

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebaseexample.data.model.Note
import com.example.firebaseexample.data.model.User
import com.example.firebaseexample.data.repository.NoteRepository
import com.example.firebaseexample.util.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository
):ViewModel() {

    private val _notes = MutableLiveData<UIState<List<Note>>>()
    val notes: LiveData<UIState<List<Note>>>
        get() = _notes

    private val _addNote = MutableLiveData<UIState<Note>>()
    val addNote: LiveData<UIState<Note>>
        get() = _addNote

    private val _updateNote = MutableLiveData<UIState<Note>>()
    val updateNote: LiveData<UIState<Note>>
        get() = _updateNote

    private val _deleteNote = MutableLiveData<UIState<Note>>()
    val deleteNote: LiveData<UIState<Note>>
        get() = _deleteNote
    fun getNotes(user: User?) {
        _notes.value = UIState.Loading

        noteRepository.getNotes(user) {
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

    /*fun onUploadSingleFile(fileUris: Uri, onResult: (UIState<Uri>) -> Unit){
        onResult.invoke(UIState.Loading)
        viewModelScope.launch {
            noteRepository.uploadSingleFile(fileUris,onResult)
        }
    }*/

    fun onUploadMultipleFile(fileUris: List<Uri>, onResult: (UIState<List<Uri>>) -> Unit){
        onResult.invoke(UIState.Loading)
        viewModelScope.launch {
            noteRepository.uploadMultipleFile(fileUris,onResult)
        }
    }
}