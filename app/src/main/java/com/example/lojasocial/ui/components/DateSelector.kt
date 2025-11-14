package com.example.lojasocial.ui.components

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DateSelector(
    startDate: Long?, // Data de início em formato Unix timestamp
    endDate: Long?, // Data de fim em formato Unix timestamp
    onStartDateSelected: (Long) -> Unit, // Callback para quando a data de início for selecionada
    onEndDateSelected: (Long) -> Unit // Callback para quando a data de fim for selecionada
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Formato de data
    val startDateFormatted = startDate?.let { dateFormat.format(Date(it)) } ?: "Início" // Formata a data de início
    val endDateFormatted = endDate?.let { dateFormat.format(Date(it)) } ?: "Fim" // Formata a data de fim

    val context = LocalContext.current // Obtém o contexto atual

    Row(
        modifier = Modifier
            .fillMaxWidth() // A linha ocupa toda a largura disponível
            .padding(vertical = 8.dp), // Adiciona margem vertical
        horizontalArrangement = Arrangement.SpaceBetween // Espaçamento entre os itens
    ) {
        // Botão para selecionar a data de início
        Text(
            text = "Início: $startDateFormatted", // Mostra a data de início formatada
            style = MaterialTheme.typography.bodyMedium.copy(
                textDecoration = TextDecoration.Underline // Adiciona sublinhado ao texto
            ),
            color = MaterialTheme.colorScheme.primary, // Cor do texto
            modifier = Modifier.clickable { // Torna o texto clicável
                showDatePicker(context) { selectedDate -> // Exibe o calendário ao clicar
                    onStartDateSelected(selectedDate) // Chama o callback com a data selecionada
                }
            }
        )

        Spacer(modifier = Modifier.width(16.dp)) // Adiciona espaçamento horizontal entre os textos

        // Botão para selecionar a data de fim
        Text(
            text = "Fim: $endDateFormatted", // Mostra a data de fim formatada
            style = MaterialTheme.typography.bodyMedium.copy(
                textDecoration = TextDecoration.Underline // Adiciona sublinhado ao texto
            ),
            color = MaterialTheme.colorScheme.primary, // Cor do texto
            modifier = Modifier.clickable { // Torna o texto clicável
                showDatePicker(context) { selectedDate -> // Exibe o calendário ao clicar
                    onEndDateSelected(selectedDate) // Chama o callback com a data selecionada
                }
            }
        )
    }
}

// Função para exibir o calendário e selecionar uma data
private fun showDatePicker(context: Context, onDateSelected: (Long) -> Unit) {
    val calendar = Calendar.getInstance() // Obtém a data atual
    DatePickerDialog(
        context, // Contexto necessário para exibir o diálogo
        { _, year, month, dayOfMonth -> // Callback para quando uma data for selecionada
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth) // Define a data selecionada no calendário
            onDateSelected(selectedCalendar.timeInMillis) // Retorna a data em formato Unix timestamp
        },
        calendar.get(Calendar.YEAR), // Ano atual
        calendar.get(Calendar.MONTH), // Mês atual
        calendar.get(Calendar.DAY_OF_MONTH) // Dia atual
    ).show() // Exibe o diálogo
}

// Função para pré-visualizar o DateSelector
@Preview(showBackground = true)
@Composable
fun DateSelectorPreview() {
    // Inicializa as datas de início e fim
    var startDate by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }
    var endDate by remember { mutableStateOf<Long?>(System.currentTimeMillis() + 86400000) }

    // Exibe o DateSelector
    DateSelector(
        startDate = startDate, // Passa a data de início
        endDate = endDate, // Passa a data de fim
        onStartDateSelected = { selectedStartDate -> // Atualiza a data de início ao selecionar
            startDate = selectedStartDate
        },
        onEndDateSelected = { selectedEndDate -> // Atualiza a data de fim ao selecionar
            endDate = selectedEndDate
        }
    )
}