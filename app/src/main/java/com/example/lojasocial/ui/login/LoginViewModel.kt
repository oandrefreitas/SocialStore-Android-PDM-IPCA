package com.example.lojasocial.ui.login

import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lojasocial.repository.UserRepository
import com.example.lojasocial.session.UserSession
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

// Estado utilizado para gerir o formulário de login
data class LoginState(
    var email: String = "",
    var password: String = "",
    var phone: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val verificationId: String? = null
)

class LoginViewModel : ViewModel() {

    var state = mutableStateOf(LoginState())
        private set

    private val email get() = state.value.email
    private val password get() = state.value.password
    private val phone get() = state.value.phone

    private val auth: FirebaseAuth = Firebase.auth

    // Atualiza o campo de e-mail
    fun onEmailChange(newValue: String) {
        state.value = state.value.copy(email = newValue)
    }

    // Atualiza o campo da palavra-passe
    fun onPasswordChange(newValue: String) {
        state.value = state.value.copy(password = newValue)
    }

    // Atualiza o campo de número de telefone
    fun onPhoneChange(newValue: String) {
        state.value = state.value.copy(phone = newValue)
    }

    // Login com e-mail e palavra-passe
    fun loginWithEmail(onLoginSuccess: () -> Unit) {
        if (email.isEmpty()) {
            state.value = state.value.copy(error = "O e-mail é obrigatório")
            return
        }
        if (password.isEmpty()) {
            state.value = state.value.copy(error = "A palavra-passe é obrigatória")
            return
        }

        state.value = state.value.copy(isLoading = true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                state.value = state.value.copy(isLoading = false)
                if (task.isSuccessful) {
                    Log.d(TAG, "Login com e-mail bem-sucedido")
                    state.value = state.value.copy(error = null)

                    // Buscar os detalhes do utilizador
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        fetchUserDetails(userId, onLoginSuccess)
                    } else {
                        state.value = state.value.copy(error = "Erro: ID do utilizador não encontrado.")
                    }
                } else {
                    Log.w(TAG, "Falha no login com e-mail", task.exception)
                    state.value = state.value.copy(error = task.exception?.message)
                }
            }
    }

    // Inicia o login com número de telefone e envia o SMS
    fun loginWithPhone(activity: Activity, onCodeSent: () -> Unit) {
        if (phone.isEmpty()) {
            state.value = state.value.copy(error = "O número de telefone é obrigatório")
            return
        }

        state.value = state.value.copy(isLoading = true, error = null)

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS) // Tempo limite para o código SMS
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Login automático ao detetar o código SMS
                    signInWithPhoneCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.w(TAG, "Falha na verificação do número de telefone", e)
                    state.value = state.value.copy(isLoading = false, error = e.message)
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    state.value = state.value.copy(isLoading = false, verificationId = verificationId)
                    onCodeSent() // Notifica que o código foi enviado
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // Verifica o código SMS fornecido pelo utilizador
    fun verifyPhoneCode(code: String, onLoginSuccess: () -> Unit) {
        val verificationId = state.value.verificationId
        if (verificationId == null) {
            state.value = state.value.copy(error = "O código de verificação é inválido")
            return
        }

        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneCredential(credential, onLoginSuccess)
    }

    // Login com credenciais fornecidas pelo telefone
    private fun signInWithPhoneCredential(
        credential: PhoneAuthCredential,
        onLoginSuccess: (() -> Unit)? = null
    ) {
        state.value = state.value.copy(isLoading = true)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                state.value = state.value.copy(isLoading = false)
                if (task.isSuccessful) {
                    Log.d(TAG, "Login com credencial de telefone bem-sucedido")
                    state.value = state.value.copy(error = null)

                    // Buscar os detalhes do utilizador
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        fetchUserDetails(userId) {
                            onLoginSuccess?.invoke()
                        }
                    } else {
                        state.value = state.value.copy(error = "Erro: ID do utilizador não encontrado.")
                    }
                } else {
                    Log.w(TAG, "Falha no login com credencial de telefone", task.exception)
                    state.value = state.value.copy(error = task.exception?.message)
                }
            }
    }

    // Recupera os detalhes do utilizador e configura a sessão
    fun fetchUserDetails(userId: String, onLoginSuccess: () -> Unit) {
        UserRepository.getUserDetails(
            userId = userId,
            onSuccess = { userDetails ->
                UserSession.setUserSession(userId = userId, accessLevel = userDetails.accessLevel)
                Log.d("LoginViewModel", "Detalhes do utilizador carregados com sucesso: ${userDetails.name}, Nível de Acesso: ${userDetails.accessLevel}")
                onLoginSuccess()
            },
            onFailure = { errorMessage ->
                state.value = state.value.copy(error = errorMessage)
                Log.e("LoginViewModel", "Erro ao carregar detalhes do utilizador: $errorMessage")
            }
        )
    }
}