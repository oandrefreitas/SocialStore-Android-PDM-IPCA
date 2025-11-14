package com.example.lojasocial.ui.welcomeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.lojasocial.R
import com.example.lojasocial.ui.theme.LojaSocialTheme

// Ecrã de boas-vindas com opções para navegar para as telas de login ou registo
@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit, // Callback para navegar para o ecrã de login
    onNavigateToRegister: () -> Unit, // Callback para navegar para o ecrã de registo
    modifier: Modifier = Modifier // Modificador para personalização
) {
    Box(
        modifier = modifier
            .fillMaxSize() // Preenche o tamanho total da tela
            .padding(16.dp), // Adiciona margem
        contentAlignment = Alignment.Center // Centraliza o conteúdo dentro da caixa
    ) {
        Column(
            modifier = Modifier.fillMaxSize(), // Preenche o tamanho total da coluna
            horizontalAlignment = Alignment.CenterHorizontally, // Alinha os elementos horizontalmente no centro
            verticalArrangement = Arrangement.Center // Centraliza os elementos verticalmente
        ) {
            // Logo da aplicação
            Image(
                painter = painterResource(id = R.drawable.logo), // Recurso de imagem para o logótipo
                contentDescription = "Logo Loja Social", // Descrição para acessibilidade
                modifier = Modifier
                    .size(200.dp) // Define o tamanho da imagem
                    .padding(bottom = 32.dp) // Espaço inferior entre o logótipo e os botões
            )

            // Botão para navegar para o ecrã de login
            Button(
                onClick = onNavigateToLogin, // Ação ao clicar no botão
                modifier = Modifier
                    .fillMaxWidth() // Preenche a largura total disponível
                    .padding(horizontal = 32.dp) // Margem horizontal
                    .height(50.dp), // Altura fixa do botão
                shape = RoundedCornerShape(10.dp) // Bordas arredondadas
            ) {
                Text(text = "Login", fontSize = 18.sp) // Texto do botão com tamanho definido
            }

            Spacer(modifier = Modifier.height(16.dp)) // Espaço entre os dois botões

            // Botão para navegar para o ecrã de registo
            Button(
                onClick = onNavigateToRegister, // Ação ao clicar no botão
                modifier = Modifier
                    .fillMaxWidth() // Preenche a largura total disponível
                    .padding(horizontal = 32.dp) // Margem horizontal
                    .height(50.dp), // Altura fixa do botão
                shape = RoundedCornerShape(10.dp) // Bordas arredondadas
            ) {
                Text(text = "Registar", fontSize = 18.sp) // Texto do botão com tamanho definido
            }
        }
    }
}

// Pré-visualização do ecrã de boas-vindas
@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    LojaSocialTheme {
        WelcomeScreen(
            onNavigateToLogin = {}, // Callback de exemplo para login
            onNavigateToRegister = {} // Callback de exemplo para registo
        )
    }
}