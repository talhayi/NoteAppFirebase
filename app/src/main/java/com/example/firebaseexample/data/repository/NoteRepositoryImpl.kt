package com.example.firebaseexample.data.repository

import com.example.firebaseexample.data.model.Note
import com.example.firebaseexample.util.FireStoreDocumentField
import com.example.firebaseexample.util.FireStoreTables
import com.example.firebaseexample.util.UIState
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NoteRepositoryImpl(
    private val database: FirebaseFirestore
) : NoteRepository {
    override fun getNotes(result: (UIState<List<Note>>) -> Unit) {
        database.collection(FireStoreTables.NOTE)
            .orderBy(FireStoreDocumentField.DATE, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                val notes = arrayListOf<Note>()
                for (document in it) {
                    val note = document.toObject(Note::class.java)
                    notes.add(note)
                }
                result.invoke(
                    UIState.Success(notes)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UIState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun addNote(note: Note, result: (UIState<Pair<Note,String>>) -> Unit) {
        val document = database.collection(FireStoreTables.NOTE).document()
        note.id = document.id
        document
            .set(note)
            .addOnSuccessListener {
                result.invoke(
                    UIState.Success(Pair(note,"Note has been created successfully"))
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UIState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun updateNote(note: Note, result: (UIState<String>) -> Unit) {
        val document: DocumentReference =
            database.collection(FireStoreTables.NOTE).document(note.id!!)
        document
            .set(note)
            .addOnSuccessListener {
                result.invoke(
                    UIState.Success("Note has been updated successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UIState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun deleteNote(note: Note, result: (UIState<String>) -> Unit) {
        val document = database.collection(FireStoreTables.NOTE).document(note.id!!)
        document
            .delete()
            .addOnSuccessListener {
                result.invoke(
                    UIState.Success("Note successfully deleted")
                )
            }
            .addOnFailureListener {e->
                result.invoke(
                    UIState.Failure(
                        e.localizedMessage
                    )
                )
            }
    }
}