package com.example.lojasocial.ui.listUsers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lojasocial.models.User

@Composable
fun ListUsersView(
    viewModel: ListUsersViewModel = viewModel(), // Instância do ViewModel para gerir o estado
    modifier: Modifier = Modifier
) {
    val state by viewModel.state // Obtenção do estado atual do ViewModel

    // Efeito que é executado assim que a composição é criada para carregar os utilizadores
    LaunchedEffect(Unit) {
        viewModel.loadUsers() // Carrega a lista de utilizadores
    }

    // Caixa principal que ocupa todo o espaço disponível
    Box(
        modifier = modifier
            .fillMaxSize() // Ocupa toda a altura e largura disponíveis
            .padding(16.dp), // Espaçamento interno
        contentAlignment = Alignment.Center // Centraliza os elementos no centro da caixa
    ) {
        // Mostra um indicador de carregamento enquanto os dados estão a ser carregados
        if (state.isLoading) {
            CircularProgressIndicator()
        }
        // Mostra uma mensagem de erro caso algo corra mal
        else if (state.error != null) {
            Text(
                text = "Erro: ${state.error}", // Mostra o texto do erro
                color = androidx.compose.material3.MaterialTheme.colorScheme.error // Define a cor do texto como erro
            )
        }
        // Mostra a lista de utilizadores caso os dados tenham sido carregados com sucesso
        else {
            ListUsersContent(
                users = state.users, // Lista de utilizadores do estado
                onUpdateAccessLevel = { userId, newAccessLevel ->
                    viewModel.updateUserAccessLevel(userId, newAccessLevel) // Atualiza o nível de acesso
                },
                onToggleActiveStatus = { userId, isActive ->
                    viewModel.toggleUserActiveStatus(userId, isActive) // Ativa ou desativa o utilizador
                }
            )
        }
    }
}

@Composable
fun ListUsersContent(
    users: List<User>, // Lista de utilizadores a serem apresentados
    onUpdateAccessLevel: (String, Int) -> Unit, // Callback para atualizar o nível de acesso
    onToggleActiveStatus: (String, Boolean) -> Unit, // Callback para alterar o estado ativo
    modifier: Modifier = Modifier
) {
    // Lista preguiçosa que exibe os utilizadores de forma eficiente
    LazyColumn(modifier = modifier.fillMaxSize()) {
        // Itera pela lista de utilizadores e cria uma linha para cada um
        items(users) { user ->
            ListUserRow(
                user = user, // Dados do utilizador atual
                onUpdateAccessLevel = { newAccessLevel ->
                    onUpdateAccessLevel(user.id, newAccessLevel) // Atualiza o nível de acesso deste utilizador
                },
                onToggleActiveStatus = { isActive ->
                    onToggleActiveStatus(user.id, isActive) // Atualiza o estado ativo deste utilizador
                }
            )
        }
    }
}