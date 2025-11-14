package com.example.lojasocial.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.lojasocial.ui.theme.LojaSocialTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String, // Título que será exibido na barra superior
    onHomeClick: () -> Unit, // Callback executado ao clicar no ícone de "Home"
    actions: @Composable (() -> Unit)? = null, // Permite adicionar ações personalizadas na barra
    onLogoffClick: (() -> Unit)? = null // Callback executado ao clicar no botão de logoff
) {
    TopAppBar(
        title = {
            // Define o título da barra, limitado a uma linha com tratamento de overflow
            Text(
                text = title,
                maxLines = 1, // Limita o texto a uma linha
                overflow = TextOverflow.Ellipsis, // Adiciona reticências se o texto for longo demais
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            // Ícone de navegação para voltar à página inicial
            IconButton(onClick = onHomeClick) {
                Icon(
                    imageVector = Icons.Filled.Home, // Ícone de "Home"
                    contentDescription = "Ir para a página inicial", // Descrição para acessibilidade
                    tint = MaterialTheme.colorScheme.onPrimary // Cor do ícone
                )
            }
        },
        actions = {
            // Adiciona o botão de logoff, caso o callback esteja definido
            if (onLogoffClick != null) {
                IconButton(onClick = onLogoffClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp, // Ícone de "Sair"
                        contentDescription = "Sair", // Descrição para acessibilidade
                        tint = MaterialTheme.colorScheme.onPrimary // Cor do ícone
                    )
                }
            }
            // Executa ações adicionais, caso tenham sido fornecidas
            actions?.invoke()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary, // Cor do fundo da barra
            titleContentColor = MaterialTheme.colorScheme.onPrimary // Cor do texto do título
        )
    )
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    LojaSocialTheme {
        // Pré-visualização do TopBar com exemplos de ações
        TopBar(
            title = "Loja Social", // Título de exemplo
            onHomeClick = { /* Ação ao clicar no ícone de Home */ },
            onLogoffClick = { /* Ação ao clicar no ícone de Sair */ },
            actions = {
                // Exemplo de uma ação adicional
                IconButton(onClick = { /* Ação adicional */ }) {
                    Icon(
                        imageVector = Icons.Default.Home, // Ícone de exemplo
                        contentDescription = "Ação adicional" // Descrição para acessibilidade
                    )
                }
            }
        )
    }
}