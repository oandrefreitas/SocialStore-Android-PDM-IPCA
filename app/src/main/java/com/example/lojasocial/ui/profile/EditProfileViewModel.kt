package com.example.lojasocial.ui.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lojasocial.repository.UserRepository
import com.example.lojasocial.session.UserSession

// Classe para representar o estado da edição do perfil
data class EditProfileState(
    val name: String = "", // Nome atual do utilizador
    val isLoading: Boolean = false, // Indica se os dados estão a ser carregados ou atualizados
    val error: String? = null, // Mensagem de erro, caso exista
    val successMessage: String? = null // Mensagem de sucesso, caso a operação seja concluída com êxito
)

class EditProfileViewModel : ViewModel() {

    // Estado atual da edição do perfil
    var state = mutableStateOf(EditProfileState())
        private set

    // Função para carregar os detalhes do utilizador
    fun loadUserDetails(userId: String) {
        state.value = state.value.copy(isLoading = true, error = null) // Atualiza o estado para indicar carregamento

        UserRepository.getUserDetails(
            userId = userId,
            onSuccess = { user ->
                // Atualiza o estado com o nome do utilizador obtido
                state.value = state.value.copy(
                    isLoading = false,
                    name = user.name
                )
            },
            onFailure = { errorMessage ->
                // Atualiza o estado com a mensagem de erro
                state.value = state.value.copy(isLoading = false, error = errorMessage)
            }
        )
    }

    // Função para atualizar o nome no estado
    fun onNameChange(newValue: String) {
        state.value = state.value.copy(name = newValue)
    }

    // Função para atualizar os dados do perfil do utilizador
    fun updateUserProfile(
        newPassword: String?, // Nova palavra-passe (opcional)
        onSuccess: () -> Unit, // Callback para quando a operação for bem-sucedida
        onFailure: (String) -> Unit // Callback para tratar erros
    ) {
        state.value = state.value.copy(isLoading = true, error = null) // Atualiza o estado para indicar carregamento

        // Obtém o ID do utilizador da sessão
        val userId = UserSession.getUserId() ?: run {
            // Se o ID não for encontrado, atualiza o estado com o erro
            state.value = state.value.copy(isLoading = false, error = "Utilizador não encontrado.")
            return
        }

        UserRepository.updateUserDetails(
            updatedName = state.value.name, // Nome atualizado do utilizador
            newPassword = if (!newPassword.isNullOrEmpty()) newPassword else null, // Palavra-passe atualizada, se fornecida
            onSuccess = {
                // Atualiza o estado com uma mensagem de sucesso
                state.value = state.value.copy(
                    isLoading = false,
                    successMessage = "Dados atualizados com sucesso!"
                )
                onSuccess() // Chama o callback de sucesso
            },
            onFailure = { errorMessage ->
                // Atualiza o estado com a mensagem de erro
                state.value = state.value.copy(isLoading = false, error = errorMessage)
                onFailure(errorMessage) // Chama o callback de erro
            }
        )
    }
}