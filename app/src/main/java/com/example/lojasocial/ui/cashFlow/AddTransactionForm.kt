package com.example.lojasocial.ui.cashFlow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.lojasocial.models.TransactionType

// Mapeia os tipos de transação para os seus rótulos correspondentes
val transactionTypeLabels = mapOf(
    TransactionType.ENTRY to "Entrada",
    TransactionType.EXIT to "Saída",
    TransactionType.ADJUSTMENT to "Ajuste"
)

@Composable
fun AddTransactionForm(
    onAddTransaction: (Double, String, TransactionType) -> Unit, // Callback para adicionar uma transação
    onCancel: () -> Unit // Callback para cancelar a operação
) {
    // Estados para armazenar os valores dos campos do formulário
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<TransactionType?>(null) }
    var expanded by remember { mutableStateOf(false) } // Controla a abertura do menu Dropdown

    // Caixa principal que envolve o formulário
    Box(
        modifier = Modifier
            .fillMaxSize() // Preenche o tamanho total disponível
            .padding(16.dp), // Margem em torno do formulário
        contentAlignment = Alignment.TopStart // Alinha o conteúdo ao topo esquerdo
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth() // Preenche a largura total
                .padding(24.dp), // Margem interna do formulário
            verticalArrangement = Arrangement.spacedBy(20.dp) // Espaço entre os elementos
        ) {
            // Título do formulário
            Text(
                text = "Adicionar Transação",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            // Campo para o valor da transação
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Valor (€)") },
                placeholder = { Text("Ex.: 100.00") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // Aceita apenas números
                modifier = Modifier.fillMaxWidth() // Preenche a largura total
            )

            // Campo para a descrição da transação
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                placeholder = { Text("Ex.: Venda de Produto") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth() // Preenche a largura total
            )

            // Dropdown para selecionar o tipo de transação
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Tipo de Transação",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Botão para abrir o Dropdown
                Box {
                    OutlinedButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = selectedType?.let { transactionTypeLabels[it] }
                                ?: "Selecione o tipo",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (selectedType != null) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }

                    // Menu Dropdown para selecionar o tipo de transação
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.width(300.dp)
                    ) {
                        TransactionType.values().forEach { type ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = transactionTypeLabels[type] ?: type.name,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                },
                                onClick = {
                                    selectedType = type
                                    expanded = false // Fecha o menu após a seleção
                                }
                            )
                        }
                    }
                }
            }

            // Botões de ação (Cancelar e Adicionar)
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Botão de cancelar
                TextButton(onClick = onCancel) {
                    Text(
                        text = "Cancelar",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Botão de adicionar
                Button(
                    onClick = {
                        val amountValue = amount.toDoubleOrNull() // Converte o valor para Double
                        if (amountValue != null && selectedType != null) {
                            onAddTransaction(amountValue, description, selectedType!!) // Chama o callback de adicionar
                        }
                    },
                    enabled = amount.isNotBlank() && description.isNotBlank() && selectedType != null // Ativa apenas se os campos forem válidos
                ) {
                    Text(
                        text = "Adicionar",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}