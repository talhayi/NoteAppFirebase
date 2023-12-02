package com.example.firebaseexample.data.repository

import android.content.SharedPreferences
import com.example.firebaseexample.data.model.User
import com.example.firebaseexample.util.FireStoreCollection
import com.example.firebaseexample.util.SharedPrefConstants
import com.example.firebaseexample.util.UIState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore,
    private val appPreferences: SharedPreferences,
    private val gson: Gson
): AuthRepository {
    override fun registerUser(
        email: String,
        password: String,
        user: User, result: (UIState<String>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    user.id = it.result.user?.uid ?: ""
                    updateUserInfo(user) { state ->
                        when(state){
                            is UIState.Success -> {
                                storeSession(id = it.result.user?.uid ?: "") {
                                    if (it == null){
                                        result.invoke(UIState.Failure("User register successfully but session failed to store"))
                                    }else{
                                        result.invoke(
                                            UIState.Success("User register successfully!")
                                        )
                                    }
                                }
                            }
                            is UIState.Failure -> {
                                result.invoke(UIState.Failure(state.error))
                            }
                            else -> {/*nothing*/}
                        }
                    }
                }else{
                    try {
                        throw it.exception ?: java.lang.Exception("Invalid authentication")
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        result.invoke(UIState.Failure("Authentication failed, Password should be at least 6 characters"))
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        result.invoke(UIState.Failure("Authentication failed, Invalid email entered"))
                    } catch (e: FirebaseAuthUserCollisionException) {
                        result.invoke(UIState.Failure("Authentication failed, Email already registered."))
                    } catch (e: Exception) {
                        result.invoke(UIState.Failure(e.message))
                    }
                }
            }
            .addOnFailureListener {
                result.invoke(
                    UIState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun updateUserInfo(user: User, result: (UIState<String>) -> Unit) {
        val document = database.collection(FireStoreCollection.USER).document(user.id!!)
        document
            .set(user)
            .addOnSuccessListener {
                result.invoke(
                    UIState.Success("User has been update successfully")
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

    override fun loginUser(
        email: String,
        password: String,
        result: (UIState<String>) -> Unit) {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storeSession(id = task.result.user?.uid ?: ""){user->
                        if (user == null){
                            result.invoke(UIState.Failure("Failed to store local session"))
                        }else{
                            result.invoke(UIState.Success("Login successfully!"))
                        }
                    }
                }
            }.addOnFailureListener {
                result.invoke(UIState.Failure("Authentication failed, Check email and password"))
            }
    }

    override fun forgotPassword(email: String, result: (UIState<String>) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.invoke(UIState.Success("Email has been sent"))

                } else {
                    result.invoke(UIState.Failure(task.exception?.message))
                }
            }.addOnFailureListener {
                result.invoke(UIState.Failure("Authentication failed, Check email"))
            }
    }

    override fun logout(result: () -> Unit) {
        auth.signOut()
        appPreferences.edit().putString(SharedPrefConstants.USER_SESSION, null).apply()
        result.invoke()
    }

    override fun storeSession(id: String, result: (User?) -> Unit) {
        database.collection(FireStoreCollection.USER).document(id)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val user = it.result.toObject(User::class.java)
                    appPreferences.edit().putString(SharedPrefConstants.USER_SESSION,gson.toJson(user)).apply()
                    result.invoke(user)
                }else{
                    result.invoke(null)
                }
            }
            .addOnFailureListener {
                result.invoke(null)
            }
    }

    override fun getSession(result: (User?) -> Unit) {
        val userStr = appPreferences.getString(SharedPrefConstants.USER_SESSION,null)
        if (userStr == null){
            result.invoke(null)
        }else{
            val user = gson.fromJson(userStr,User::class.java)
            result.invoke(user)
        }
    }
}