package com.example.firebaseexample.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firebaseexample.data.model.User
import com.example.firebaseexample.data.repository.AuthRepository
import com.example.firebaseexample.util.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _register = MutableLiveData<UIState<String>>()
    val register: LiveData<UIState<String>>
        get() = _register

    fun register(
        email: String,
        password: String,
        user: User
    ) {
        _register.value = UIState.Loading
        authRepository.registerUser(
            email = email,
            password = password,
            user = user
        ) { _register.value = it }
    }
}