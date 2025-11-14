package com.example.lojasocial.ui.login

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lojasocial.R
import com.example.lojasocial.ui.theme.LojaSocialTheme

@Composable
fun LoginView(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit = {}
) {
    // Inicializa o ViewModel e o estado
    val viewModel by remember { mutableStateOf(LoginViewModel()) }
    val state by viewModel.state
    val context = LocalContext.current as Activity

    // Variável para alternar entre os métodos de login
    var selectedMethod by remember { mutableStateOf("email") }
    var verificationCode by remember { mutableStateOf("") }

    // Caixa principal que contém o layout da vista de login
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Exibe o logótipo da aplicação
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo Loja Social",
                modifier = Modifier
                    .size(200.dp)
                    .padding(top = 30.dp)
                    .padding(bottom = 20.dp)
            )

            // Texto para selecionar o método de login
            Text(
                text = "Método de Login:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            // Botões para alternar entre login por e-mail e por telefone
            Row {
                Button(
                    onClick = { selectedMethod = "email" },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Login com E-mail")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { selectedMethod = "phone" },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Login com Telefone")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Apresenta os campos e botões com base no método selecionado
            when (selectedMethod) {
                "email" -> {
                    // Campos para login com e-mail e palavra-passe
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = viewModel::onEmailChange,
                        label = { Text("E-mail") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.password,
                        onValueChange = viewModel::onPasswordChange,
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Botão para iniciar sessão com e-mail
                    Button(
                        onClick = {
                            viewModel.loginWithEmail {
                                onLoginSuccess()
                            }
                        },
                        enabled = !state.isLoading && state.email.isNotEmpty() && state.password.isNotEmpty(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Login com E-mail")
                    }
                }

                "phone" -> {
                    // Campos para login com número de telefone
                    OutlinedTextField(
                        value = state.phone,
                        onValueChange = viewModel::onPhoneChange,
                        label = { Text("Número de Telefone") },
                        placeholder = { Text("Ex.: Inserir Indicativo (+351)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Botão para enviar o código de verificação
                    Button(
                        onClick = {
                            viewModel.loginWithPhone(
                                activity = context,
                                onCodeSent = {
                                }
                            )
                        },
                        enabled = !state.isLoading && state.phone.isNotEmpty(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Enviar Código")
                    }

                    // Campos e botão adicionais para verificação do código
                    if (state.verificationId != null) {
                        Spacer(modifier = Modifier.height(16.dp))

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
                                viewModel.verifyPhoneCode(code = verificationCode) {
                                    onLoginSuccess()
                                }
                            },
                            enabled = verificationCode.isNotEmpty(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Verificar Código")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Indicador de carregamento
            if (state.isLoading) {
                CircularProgressIndicator()
            }

            // Mensagem de erro
            if (state.error != null) {
                Text(
                    text = "Erro: ${state.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginViewPreview() {
    LojaSocialTheme {
        LoginView()
    }
}