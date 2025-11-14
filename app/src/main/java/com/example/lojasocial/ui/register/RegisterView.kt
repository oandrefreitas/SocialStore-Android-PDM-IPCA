package com.example.lojasocial.ui.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lojasocial.R

@Composable
fun RegisterView(
    onRegisterSuccess: () -> Unit, // Callback para ser chamado quando o registo for bem-sucedido
    viewModel: RegisterViewModel = viewModel(), // ViewModel para gerir o estado e operações de registo
    modifier: Modifier = Modifier // Modificador padrão para customizar a composição
) {
    // Estado da interface, que inclui o método selecionado (e-mail ou telefone)
    val state by viewModel.state
    var selectedMethod by remember { mutableStateOf("email") } // Método de registo selecionado
    var verificationCode by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize() // Preenche todo o espaço disponível
            .padding(16.dp), // Margem em torno da interface
        contentAlignment = Alignment.TopCenter // Centraliza o conteúdo no topo
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(), // O conteúdo ocupa toda a largura disponível
            verticalArrangement = Arrangement.Center, // Espaçamento vertical uniforme
            horizontalAlignment = Alignment.CenterHorizontally // Centraliza horizontalmente
        ) {
            // Exibe o logótipo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo Loja Social", // Texto alternativo para acessibilidade
                modifier = Modifier
                    .size(200.dp) // Tamanho do logótipo
                    .padding(top = 10.dp, bottom = 10.dp) // Margem superior e inferior
            )

            // Texto informativo para escolher o método de registo
            Text(
                text = "Método de Registo:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Botões para selecionar o método de registo (e-mail ou telefone)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly // Espaçamento uniforme entre os botões
            ) {
                Button(
                    onClick = { selectedMethod = "email" },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp), // Margem horizontal entre os botões
                    shape = RoundedCornerShape(10.dp) // Cantos arredondados
                ) {
                    Text(text = "E-mail")
                }
                Button(
                    onClick = { selectedMethod = "phone" },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    shape = RoundedCornerShape(10.dp) // Cantos arredondados
                ) {
                    Text(text = "Telefone")
                }
            }

            Spacer(modifier = Modifier.height(16.dp)) // Espaçamento vertical

            // Campos de entrada baseados no método selecionado
            OutlinedTextField(
                value = state.name, // Nome do utilizador
                onValueChange = viewModel::onNameChange, // Atualiza o estado do nome
                label = { Text("Nome") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Registo por e-mail
            if (selectedMethod == "email") {
                OutlinedTextField(
                    value = state.email, // E-mail do utilizador
                    onValueChange = viewModel::onEmailChange, // Atualiza o estado do e-mail
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.password, // Palavra-passe
                    onValueChange = viewModel::onPasswordChange, // Atualiza o estado da palavra-passe
                    label = { Text("Password") },
                    placeholder = { Text("Ex.: Min. 6 Caracteres") },
                    visualTransformation = PasswordVisualTransformation(), // Esconde a palavra-passe
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botão para registar com e-mail
                Button(
                    onClick = {
                        if (state.name.isNotBlank() && state.email.isNotBlank() && state.password.isNotBlank()) {
                            viewModel.registerWithEmail(onRegisterSuccess)
                        } else {
                            viewModel.setErrorMessage("Por favor, preencha todos os campos obrigatórios.")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(50.dp), // Altura fixa
                    shape = RoundedCornerShape(10.dp), // Cantos arredondados
                    enabled = !state.isLoading // Ativa ou desativa com base no estado de carregamento
                ) {
                    Text("Registar")
                }
            }
            // Registo por telefone
            else {
                OutlinedTextField(
                    value = state.phone, // Número de telefone
                    onValueChange = viewModel::onPhoneChange, // Atualiza o estado do telefone
                    label = { Text("Número de Telefone") },
                    placeholder = { Text("Ex.: Inserir Indicativo (+351)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))


                if (state.verificationId != null) {
                    OutlinedTextField(
                        value = verificationCode,
                        onValueChange = { verificationCode = it },
                        label = { Text("Código de Verificação") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (verificationCode.isNotBlank()) {
                                viewModel.verifyPhoneCode(
                                    code = verificationCode,
                                    onRegisterSuccess = onRegisterSuccess
                                )
                            } else {
                                viewModel.setErrorMessage("Por favor, insira o código de verificação.")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(10.dp),
                        enabled = verificationCode.isNotEmpty()
                    ) {
                        Text("Verificar Código")
                    }
                } else {
                    Button(
                        onClick = {
                            if (state.name.isNotBlank() && state.phone.isNotBlank()) {
                                viewModel.registerWithPhone(onRegisterSuccess)
                            } else {
                                viewModel.setErrorMessage("Por favor, preencha todos os campos obrigatórios.")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !state.isLoading
                    ) {
                        Text("Enviar Código")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                CircularProgressIndicator()
            }

            state.error?.let {
                Text(
                    text = "Erro: $it",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            state.successMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}