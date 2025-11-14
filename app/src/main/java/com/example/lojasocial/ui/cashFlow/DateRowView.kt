package com.example.lojasocial.ui.cashFlow

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lojasocial.ui.components.DateSelector

/**
 * Componente que exibe uma linha para seleção de datas, incluindo um título e um seletor de intervalo de datas.
 *
 * @param currentStartDate Data inicial atualmente selecionada, ou null se nenhuma estiver selecionada.
 * @param currentEndDate Data final atualmente selecionada, ou null se nenhuma estiver selecionada.
 * @param onStartDateSelected Callback chamado quando uma nova data inicial é selecionada.
 * @param onEndDateSelected Callback chamado quando uma nova data final é selecionada.
 */
@Composable
fun DateRowView(
    currentStartDate: Long?,
    currentEndDate: Long?,
    onStartDateSelected: (Long) -> Unit,
    onEndDateSelected: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth() // Preenche toda a largura disponível
            .padding(16.dp), // Adiciona preenchimento ao redor da coluna
        verticalArrangement = Arrangement.spacedBy(8.dp) // Espaçamento vertical entre os itens da coluna
    ) {
        // Texto para o título da linha de seleção de datas
        Text(
            text = "Transações por Datas", // Título
            style = MaterialTheme.typography.titleMedium, // Estilo do texto, definido no tema
            color = MaterialTheme.colorScheme.primary // Cor do texto, definida no tema
        )

        // Componente para seleção de intervalo de datas
        DateSelector(
            startDate = currentStartDate, // Data inicial atualmente selecionada
            endDate = currentEndDate, // Data final atualmente selecionada
            onStartDateSelected = onStartDateSelected, // Callback quando uma nova data inicial é selecionada
            onEndDateSelected = onEndDateSelected // Callback quando uma nova data final é selecionada
        )
    }
}

/**
 * Pré-visualização do componente `DateRowView`, com valores de exemplo.
 */
@Preview(showBackground = true)
@Composable
fun DateRowViewPreview() {
    // Estado para a data inicial e final, usando valores predefinidos
    var startDate by remember { mutableStateOf(System.currentTimeMillis()) } // Data atual
    var endDate by remember { mutableStateOf(System.currentTimeMillis() + 86400000) } // Data atual + 1 dia

    // Invocação do componente com valores de exemplo
    DateRowView(
        currentStartDate = startDate, // Passa a data inicial
        currentEndDate = endDate, // Passa a data final
        onStartDateSelected = { selectedStartDate -> // Callback para atualizar a data inicial
            startDate = selectedStartDate
        },
        onEndDateSelected = { selectedEndDate -> // Callback para atualizar a data final
            endDate = selectedEndDate
        }
    )
}
