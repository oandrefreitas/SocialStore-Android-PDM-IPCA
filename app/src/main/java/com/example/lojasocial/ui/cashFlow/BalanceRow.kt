package com.example.lojasocial.ui.cashFlow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lojasocial.R
import com.example.lojasocial.ui.theme.LojaSocialTheme

@Composable
fun BalanceRow(balance: Double) {
    // Caixa principal para exibir o saldo
    Box(
        modifier = Modifier
            .fillMaxWidth() // A largura preenche toda a linha disponível
            .padding(16.dp) // Adiciona espaçamento em torno da caixa
            .background(
                color = MaterialTheme.colorScheme.primaryContainer, // Define a cor de fundo da caixa
                shape = RoundedCornerShape(16.dp) // Cantos arredondados
            )
            .padding(24.dp), // Margem interna para o conteúdo
        contentAlignment = Alignment.Center // Centraliza o conteúdo dentro da caixa
    ) {
        // Coluna que organiza o ícone e o texto verticalmente
        Column(
            horizontalAlignment = Alignment.CenterHorizontally // Centraliza os elementos horizontalmente
        ) {
            // Ícone representativo do saldo
            Icon(
                painter = painterResource(id = R.drawable.bank), // Ícone de um recurso gráfico
                contentDescription = "Saldo Atual", // Descrição para leitores de ecrã
                tint = MaterialTheme.colorScheme.onPrimaryContainer, // Cor do ícone
                modifier = Modifier.size(48.dp) // Define o tamanho do ícone
            )
            Spacer(modifier = Modifier.height(8.dp)) // Espaço entre o ícone e o texto

            // Texto que exibe o saldo atual
            Text(
                text = "Saldo Atual: ${"%.2f".format(balance)} €", // Formata o saldo com duas casas decimais
                style = MaterialTheme.typography.titleLarge, // Estilo do texto
                color = MaterialTheme.colorScheme.onPrimaryContainer // Cor do texto
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BalanceRowPreview() {
    LojaSocialTheme {
        // Exemplo de saldo para o modo de pré-visualização
        BalanceRow(balance = 1234.56)
    }
}