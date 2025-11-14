package com.example.lojasocial.ui.register

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lojasocial.models.User
import com.example.lojasocial.repository.UserRepository
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

// Define o estado do formulário de registo de utilizadores
data class AddUserState(
    var name: String = "", // Nome do utilizador
    var email: String = "", // E-mail do utilizador
    var password: String = "", // Palavra-passe do utilizador
    var phone: String = "", // Número de telefone do utilizador
    val isLoading: Boolean = false, // Indica se a operação está a ser processada
    val error: String? = null, // Mensagem de erro (caso exista)
    val successMessage: String? = null, // Mensagem de sucesso (caso exista)
    val verificationId: String? = null // ID de verificação para autenticação por telefone
)

// ViewModel para gerir o registo de utilizadores
class RegisterViewModel : ViewModel() {

    // Estado atual do formulário
    var state = mutableStateOf(AddUserState())
        private set

    // Define uma mensagem de erro no estado
    fun setErrorMessage(message: String) {
        state.value = state.value.copy(error = message)
    }

    // Propriedades auxiliares para facilitar o acesso aos campos
    private val name get() = state.value.name
    private val email get() = state.value.email
    private val password get() = state.value.password
    private val phone get() = state.value.phone

    // Atualiza o nome no estado
    fun onNameChange(newValue: String) {
        state.value = state.value.copy(name = newValue)
    }

    // Atualiza o e-mail no estado
    fun onEmailChange(newValue: String) {
        state.value = state.value.copy(email = newValue)
    }

    // Atualiza a palavra-passe no estado
    fun onPasswordChange(newValue: String) {
        state.value = state.value.copy(password = newValue)
    }

    // Atualiza o número de telefone no estado
    fun onPhoneChange(newValue: String) {
        state.value = state.value.copy(phone = newValue)
    }

    // Função para registar um utilizador com e-mail e palavra-passe
    fun registerWithEmail(onRegisterSuccess: () -> Unit) {
        // Atualiza o estado para indicar que a operação está a ser processada
        state.value = state.value.copy(isLoading = true, error = null, successMessage = null)

        // Chama o repositório para criar o utilizador com e-mail e palavra-passe
        UserRepository.registerWithEmail(
            email = email,
            password = password,
            name = name,
            phone = phone,
            accessLevel = 2, // Define o nível de acesso como "Voluntário" por padrão
            isActive = true,
            onAddSuccess = {
                state.value = state.value.copy(
                    isLoading = false,
                    successMessage = "Utilizador adicionado com sucesso!"
                )
                Log.d("AddUserViewModel", "Utilizador adicionado com sucesso")
                onRegisterSuccess() // Callback de sucesso para navegação ou feedback
            },
            onAddFailure = { errorMessage ->
                state.value = state.value.copy(isLoading = false, error = errorMessage)
                Log.e("RegisterViewModel", "Erro ao adicionar utilizador: $errorMessage")
            }
        )
    }

    // Função para iniciar o registo por telefone
    fun registerWithPhone(onRegisterSuccess: () -> Unit) {
        // Atualiza o estado para indicar que a operação está a ser processada
        state.value = state.value.copy(isLoading = true, error = null)

        // Chama o repositório para enviar o código de verificação
        UserRepository.registerWithPhone(
            phoneNumber = state.value.phone,
            onCodeSent = { verificationId, _ ->
                state.value = state.value.copy(isLoading = false, verificationId = verificationId)
            },
            onVerificationCompleted = { credential ->
                saveUserWithPhoneCredential(credential, onRegisterSuccess)
            },
            onVerificationFailed = { errorMessage ->
                state.value = state.value.copy(isLoading = false, error = errorMessage)
            }
        )
    }

    // Função para verificar o código manualmente
    fun verifyPhoneCode(code: String, onRegisterSuccess: () -> Unit) {
        val verificationId = state.value.verificationId
        if (verificationId != null) {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            saveUserWithPhoneCredential(credential, onRegisterSuccess)
        } else {
            state.value = state.value.copy(error = "ID de verificação não encontrado")
        }
    }

    // Função para guardar o utilizador após a verificação por telefone
    private fun saveUserWithPhoneCredential(
        credential: PhoneAuthCredential,
        onRegisterSuccess: () -> Unit
    ) {
        // Atualiza o estado para indicar que a operação está a ser processada
        state.value = state.value.copy(isLoading = true)

        // Cria o objeto do utilizador
        val user = User(
            id = "", // O ID será atribuído automaticamente
            name = name,
            phone = phone,
            email = null, // Não é necessário e-mail para registo por telefone
            accessLevel = 2, // Define o nível de acesso como "Voluntário" por padrão
            isActive = true
        )

        // Chama o repositório para guardar o utilizador no Firestore
        UserRepository.saveUserWithPhoneCredential(
            name = user.name,
            phone = user.phone!!,
            credential = credential,
            accessLevel = user.accessLevel,
            isActive = user.isActive,
            onAddSuccess = {
                state.value = state.value.copy(
                    isLoading = false,
                    successMessage = "Utilizador adicionado com sucesso!"
                )
                Log.d("RegisterViewModel", "Utilizador adicionado com sucesso")
                onRegisterSuccess() // Callback de sucesso para navegação ou feedback
            },
            onAddFailure = { errorMessage ->
                state.value = state.value.copy(isLoading = false, error = errorMessage)
                Log.e("RegisterViewModel", "Erro ao adicionar utilizador: $errorMessage")
            }
        )

    }
}

