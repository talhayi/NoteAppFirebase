package com.example.firebaseexample.data.repository

import android.net.Uri
import com.example.firebaseexample.data.model.Note
import com.example.firebaseexample.data.model.User
import com.example.firebaseexample.util.FireStoreDocumentField
import com.example.firebaseexample.util.FireStoreTables
import com.example.firebaseexample.util.FirebaseStorageConstants.NOTE_IMAGES
import com.example.firebaseexample.util.UIState
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class NoteRepositoryImpl(
    private val database: FirebaseFirestore,
    private val storageReference: StorageReference
) : NoteRepository {
    override fun getNotes(user: User?, result: (UIState<List<Note>>) -> Unit) {
        database.collection(FireStoreTables.NOTE)
            .whereEqualTo(FireStoreDocumentField.USER_ID, user?.id)
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

    override suspend fun uploadSingleFile(fileUri: Uri, onResult: (UIState<Uri>) -> Unit) {
        try {
            val uri: Uri = withContext(Dispatchers.IO) {
                storageReference
                    .putFile(fileUri)
                    .await()
                    .storage
                    .downloadUrl
                    .await()
            }
            onResult.invoke(UIState.Success(uri))
        } catch (e: FirebaseException){
            onResult.invoke(UIState.Failure(e.message))
        }catch (e: Exception){
            onResult.invoke(UIState.Failure(e.message))
        }
    }

    override suspend fun uploadMultipleFile(
        fileUri: List<Uri>,
        onResult: (UIState<List<Uri>>) -> Unit
    ) {
        try {
            val uri: List<Uri> = withContext(Dispatchers.IO) {
                fileUri.map { image ->
                    async {
                        storageReference.child(NOTE_IMAGES).child(image.lastPathSegment ?: "${System.currentTimeMillis()}")
                            .putFile(image)
                            .await()
                            .storage
                            .downloadUrl
                            .await()
                    }
                }.awaitAll()
            }
            onResult.invoke(UIState.Success(uri))
        } catch (e: FirebaseException){
            onResult.invoke(UIState.Failure(e.message))
        }catch (e: Exception){
            onResult.invoke(UIState.Failure(e.message))
        }
    }
}