package com.example.lojasocial.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lojasocial.session.UserSession

@Composable
fun EditProfileView(
    targetUserId: String? = null,
    onProfileUpdated: () -> Unit = {},
    viewModel: EditProfileViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    // Carregar os detalhes do utilizador quando a vista é apresentada
    LaunchedEffect(targetUserId) {
        val userId = targetUserId ?: UserSession.getUserId() // Usa o ID do utilizador em sessão, se o ID alvo não for fornecido
        if (userId != null) {
            viewModel.loadUserDetails(userId) // Carrega os detalhes do utilizador
        }
    }

    val state by viewModel.state

    Column(
        modifier = modifier
            .fillMaxSize() // Preenche a largura e altura disponíveis
            .padding(16.dp), // Adiciona margens em todos os lados
        verticalArrangement = Arrangement.Top, // Alinha os elementos no topo
        horizontalAlignment = Alignment.Start // Alinha os elementos à esquerda
    ) {

        Spacer(modifier = Modifier.height(16.dp)) // Espaçamento inicial

        if (state.isLoading) {
            // Mostra um indicador de carregamento enquanto os dados são carregados
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            // Campo de texto para o nome
            OutlinedTextField(
                value = state.name, // Valor atual do nome
                onValueChange = { viewModel.onNameChange(it) }, // Atualiza o nome no estado do ViewModel
                label = { Text("Nome") }, // Rótulo para o campo de entrada
                modifier = Modifier.fillMaxWidth() // Faz com que o campo ocupe toda a largura disponível
            )

            Spacer(modifier = Modifier.height(12.dp)) // Espaçamento entre os campos

            // Campo de texto para a nova palavra-passe (opcional)
            var newPassword by remember { mutableStateOf("") } // Estado local para armazenar a nova palavra-passe
            OutlinedTextField(
                value = newPassword, // Valor atual da palavra-passe
                onValueChange = { newPassword = it }, // Atualiza o valor da palavra-passe
                label = { Text("Nova Senha (opcional)") }, // Rótulo para o campo de entrada
                visualTransformation = PasswordVisualTransformation(), // Oculta o texto digitado para maior segurança
                modifier = Modifier.fillMaxWidth() // Faz com que o campo ocupe toda a largura disponível
            )

            Spacer(modifier = Modifier.height(24.dp)) // Espaçamento entre os campos e o botão

            // Botão para guardar as alterações
            Button(
                onClick = {
                    viewModel.updateUserProfile(
                        newPassword = newPassword, // Envia a nova palavra-passe, se fornecida
                        onSuccess = { onProfileUpdated() }, // Callback para tratar o sucesso da atualização
                        onFailure = { errorMessage ->
                            // Atualiza o estado com a mensagem de erro
                            viewModel.state.value = viewModel.state.value.copy(error = errorMessage)
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth() // Faz com que o botão ocupe toda a largura disponível
                    .padding(horizontal = 32.dp) // Adiciona margens horizontais
                    .height(50.dp), // Define a altura do botão
                shape = RoundedCornerShape(10.dp), // Bordas arredondadas
            ) {
                Text("Guardar Alterações") // Texto do botão
            }

            Spacer(modifier = Modifier.height(12.dp)) // Espaçamento entre o botão e as mensagens

            // Mostra mensagens de erro, caso existam
            if (state.error != null) {
                Text(
                    text = "Erro: ${state.error}", // Mensagem de erro
                    color = MaterialTheme.colorScheme.error, // Cor vermelha para indicar erro
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Mostra mensagem de sucesso, caso exista
            state.successMessage?.let {
                Text(
                    text = it, // Mensagem de sucesso
                    color = MaterialTheme.colorScheme.primary, // Cor primária para indicar sucesso
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}