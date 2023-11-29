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
    fun getNotes() {
        _notes.value = UIState.Loading
        _notes.value = noteRepository.getNotes()
    }
}