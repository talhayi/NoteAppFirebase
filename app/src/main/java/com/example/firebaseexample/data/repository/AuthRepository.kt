package com.example.firebaseexample.data.repository

import com.example.firebaseexample.data.model.User
import com.example.firebaseexample.util.UIState

interface AuthRepository {
    fun registerUser(email: String, password: String, user: User, result: (UIState<String>) -> Unit)
    fun updateUserInfo(user: User, result: (UIState<String>) -> Unit)
    fun loginUser(email: String, password: String, result: (UIState<String>) -> Unit)
    fun forgotPassword(email: String, result: (UIState<String>) -> Unit)
    fun logout(result: () -> Unit)
    fun storeSession(id: String, result: (User?) -> Unit)
    fun getSession(result: (User?) -> Unit)
}