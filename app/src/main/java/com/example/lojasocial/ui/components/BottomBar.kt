package com.example.lojasocial.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.lojasocial.ui.theme.LojaSocialTheme

/**
 * Componente para a barra inferior (BottomBar) da aplicação.
 */
@Composable
fun BottomBar() {
    BottomAppBar(
        modifier = Modifier.height(52.dp), // Define a altura da barra inferior
        containerColor = MaterialTheme.colorScheme.primary, // Define a cor de fundo da barra
        contentColor = MaterialTheme.colorScheme.onPrimary // Define a cor dos conteúdos (ícones/textos) da barra
    ) {
        // Espaço flexível para centralizar os ícones ou outros conteúdos
        Spacer(modifier = Modifier.weight(1f))
    }
}

/**
 * Função de pré-visualização da BottomBar para testes visuais no Android Studio.
 */
@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {
    LojaSocialTheme {
        BottomBar() // Mostra a BottomBar com o tema definido
    }
}
