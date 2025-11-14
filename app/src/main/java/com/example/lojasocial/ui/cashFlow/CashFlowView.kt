package com.example.lojasocial.ui.cashFlow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lojasocial.models.CashTransaction

@Composable
fun CashFlowView(viewModel: CashFlowViewModel = viewModel()) {
    // Obtemos o estado atual da interface do utilizador
    val uiState by viewModel.uiState.collectAsState()

    // Controla se o formulário para adicionar transações está visível
    var showForm by remember { mutableStateOf(false) }

    // Verifica se o formulário deve ser exibido ou se deve mostrar o conteúdo principal
    if (showForm) {
        // Exibe o formulário para adicionar transações
        AddTransactionForm(
            onAddTransaction = { amount, description, type ->
                viewModel.addTransaction(amount, description, type) // Adiciona a transação
                showForm = false // Fecha o formulário após adicionar
            },
            onCancel = { showForm = false } // Fecha o formulário sem adicionar
        )
    } else {
        // Exibe o conteúdo principal
        CashFlowContent(
            balance = uiState.balance, // Saldo atual
            transactions = uiState.transactions, // Lista de transações
            selectedStartDate = uiState.startDate, // Data de início selecionada
            selectedEndDate = uiState.endDate, // Data de fim selecionada
            onAddTransaction = { showForm = true }, // Mostra o formulário ao adicionar transação
            onDateRangeSelected = { startDate, endDate ->
                viewModel.loadTransactionsByDateRange(startDate, endDate) // Filtra as transações por intervalo de datas
            }
        )
    }
}

@Composable
fun CashFlowContent(
    balance: Double, // Saldo atual
    transactions: List<CashTransaction>, // Lista de transações
    selectedStartDate: Long?, // Data de início selecionada
    selectedEndDate: Long?, // Data de fim selecionada
    onAddTransaction: () -> Unit, // Callback para adicionar transação
    onDateRangeSelected: (Long, Long) -> Unit // Callback para selecionar intervalo de datas
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Linha de Saldo
        BalanceRow(balance = balance)

        Spacer(modifier = Modifier.height(16.dp))

        // Botão para adicionar transações
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            // Botão circular com ícone de adicionar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onAddTransaction) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar transação",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Texto ao lado do botão de adicionar
            Text(
                text = "Adicionar Transação",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Linha de seleção de datas
        DateRowView(
            currentStartDate = selectedStartDate,
            currentEndDate = selectedEndDate,
            onStartDateSelected = { startDate ->
                // Atualiza o intervalo de datas ao selecionar a data inicial
                onDateRangeSelected(startDate, selectedEndDate ?: startDate)
            },
            onEndDateSelected = { endDate ->
                // Atualiza o intervalo de datas ao selecionar a data final
                onDateRangeSelected(selectedStartDate ?: endDate, endDate)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de transações
        LazyColumn(
            modifier = Modifier.weight(1f), // A lista ocupa o espaço restante
            verticalArrangement = Arrangement.spacedBy(8.dp) // Espaçamento entre os itens da lista
        ) {
            // Verifica se existem transações
            if (transactions.isEmpty()) {
                // Mensagem de ausência de transações
                item {
                    Text(
                        text = "Nenhuma transação encontrada.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Exibe cada transação na lista
                items(transactions) { transaction ->
                    TransactionRow(transaction = transaction)
                }
            }
        }
    }
}