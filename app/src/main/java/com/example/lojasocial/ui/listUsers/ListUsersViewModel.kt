package com.example.lojasocial.ui.listUsers

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.User
import com.example.lojasocial.repository.UserRepository
import kotlinx.coroutines.launch

// Estado para gerir a lista de utilizadores e a interface
data class ListUsersState(
    val users: List<User> = emptyList(), // Lista de utilizadores
    val isLoading: Boolean = false, // Indicador de carregamento
    val error: String? = null // Mensagem de erro
)

class ListUsersViewModel : ViewModel() {

    var state = mutableStateOf(ListUsersState()) // Estado inicial
        private set

    // Carrega a lista de utilizadores a partir do repositório
    fun loadUsers() {
        state.value = state.value.copy(isLoading = true, error = null) // Atualiza o estado para carregamento

        UserRepository.getAllUsers(
            onSuccess = { users ->
                state.value = state.value.copy(users = users, isLoading = false) // Atualiza a lista de utilizadores
            },
            onFailure = { errorMessage ->
                state.value = state.value.copy(isLoading = false, error = errorMessage) // Atualiza o estado com a mensagem de erro
            }
        )
    }

    // Atualiza o nível de acesso de um utilizador
    fun updateUserAccessLevel(userId: String, newAccessLevel: Int) {
        viewModelScope.launch {
            UserRepository.updateUserAccessLevel(
                userId = userId, // ID do utilizador
                newAccessLevel = newAccessLevel, // Novo nível de acesso
                onSuccess = {
                    loadUsers() // Recarrega a lista de utilizadores para refletir as alterações
                },
                onFailure = { errorMessage ->
                    state.value = state.value.copy(error = errorMessage) // Atualiza o estado com a mensagem de erro
                }
            )
        }
    }

    // Ativa ou desativa o estado de um utilizador
    fun toggleUserActiveStatus(userId: String, isActive: Boolean) {
        viewModelScope.launch {
            UserRepository.updateUserStatus(
                userId = userId, // ID do utilizador
                isActive = isActive, // Novo estado (ativo ou inativo)
                onSuccess = {
                    loadUsers() // Recarrega a lista de utilizadores para refletir as alterações
                },
                onFailure = { errorMessage ->
                    state.value = state.value.copy(error = errorMessage) // Atualiza o estado com a mensagem de erro
                }
            )
        }
    }
}