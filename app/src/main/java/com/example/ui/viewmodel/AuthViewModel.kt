package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isAuthenticated: Boolean = false,
    val adminName: String = "مستر مصطفى فرغلي",
    val role: String = "مسؤول أمن الإدارة التعليمية",
    val administration: String = "إدارة سمالوط التعليمية",
    val email: String = "security.samalut@edu.eg",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(emailOrUser: String, pass: String, rememberMe: Boolean = true) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
            kotlinx.coroutines.delay(400) // Realistic UX auth transition

            val trimmedUser = emailOrUser.trim()
            val trimmedPass = pass.trim()

            // Flexible admin validation: accepts admin credentials, email, or passcode
            if (trimmedUser.isBlank() || trimmedPass.isBlank()) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    errorMessage = "يرجى إدخال اسم المستخدم أو البريد وكلمة المرور"
                )
                return@launch
            }

            if (trimmedPass.length < 4) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    errorMessage = "كلمة المرور يجب أن تكون 4 رموز على الأقل"
                )
                return@launch
            }

            // Success
            _authState.value = _authState.value.copy(
                isAuthenticated = true,
                isLoading = false,
                errorMessage = null,
                adminName = if (trimmedUser.contains("مصطفى") || trimmedUser.contains("فرغلي") || trimmedUser.equals("admin", ignoreCase = true)) {
                    "مستر مصطفى فرغلي"
                } else {
                    trimmedUser
                }
            )
        }
    }

    fun quickAdminLogin() {
        _authState.value = _authState.value.copy(
            isAuthenticated = true,
            adminName = "مستر مصطفى فرغلي",
            isLoading = false,
            errorMessage = null
        )
    }

    fun logout() {
        _authState.value = _authState.value.copy(
            isAuthenticated = false,
            errorMessage = null
        )
    }

    fun clearError() {
        _authState.value = _authState.value.copy(errorMessage = null)
    }
}
