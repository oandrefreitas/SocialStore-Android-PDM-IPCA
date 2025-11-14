package com.example.lojasocial.ui.cashFlow

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.lojasocial.models.CashTransaction
import com.example.lojasocial.models.TransactionType
import java.text.SimpleDateFormat
import java.util.*

/**
 * Componente que representa uma linha com os detalhes de uma transação monetária.
 *
 * @param transaction Objeto que contém os dados da transação a serem exibidos.
 */
@Composable
fun TransactionRow(transaction: CashTransaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth() // Ocupa toda a largura disponível
            .padding(vertical = 8.dp), // Espaçamento vertical entre as linhas
        horizontalArrangement = Arrangement.SpaceBetween // Distribui o conteúdo com espaço entre as colunas
    ) {
        // Coluna para exibir a descrição e a data/hora da transação
        Column {
            Text(
                text = transaction.description, // Descrição da transação
                style = MaterialTheme.typography.bodyLarge // Estilo do texto principal
            )
            Text(
                text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(Date(transaction.timestamp)), // Formata o timestamp da transação
                style = MaterialTheme.typography.bodySmall, // Estilo do texto secundário
                color = MaterialTheme.colorScheme.onSurfaceVariant // Cor do texto
            )
        }

        // Texto para exibir o valor da transação
        Text(
            text = "€${"%.2f".format(transaction.amount)}", // Formata o valor em euros com 2 casas decimais
            color = when (transaction.type) { // Define a cor com base no tipo de transação
                TransactionType.ENTRY -> Color.Green // Entrada: cor verde
                TransactionType.EXIT -> Color.Red // Saída: cor vermelha
                TransactionType.ADJUSTMENT -> Color.Blue // Ajuste: cor azul
            },
            style = MaterialTheme.typography.bodyLarge // Estilo do texto do valor
        )
    }
}