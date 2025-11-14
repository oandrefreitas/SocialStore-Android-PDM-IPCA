package com.example.lojasocial.ui.visita

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lojasocial.models.Visita
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VisitaListScreen(
    visitas: List<Visita>, // Lista de visitas a ser exibida
    onBack: () -> Unit // Callback para navegar para a página anterior
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) // Formatação de data e hora

    Column(
        modifier = Modifier
            .fillMaxSize() // Ocupar todo o espaço disponível
            .padding(16.dp) // Margem interna de 16dp
    ) {

        var totalVisitas = 0
        for (visita in visitas) {
            totalVisitas += 1
        }

        Text(
            text = "Visitas Registadas: $totalVisitas", // Título da página
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp) // Espaçamento inferior
        )

        // Lista de visitas
        LazyColumn {
            items(visitas) { visita ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth() // Ocupar a largura total do pai
                        .padding(vertical = 4.dp), // Espaçamento entre cartões
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary) // Fundo azul
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Formatação da data da visita
                        val formattedDate = dateFormat.format(Date(visita.data))

                        // Exibição da data
                        Row {
                            Text(
                                text = "Data: ",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), // Negrito
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        // Exibição das notas
                        Row {
                            Text(
                                text = "Notas: ",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), // Negrito
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = visita.notas,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // Espaço entre a lista e o botão

        // Botão para voltar
        Button(
            onClick = { onBack() }, // Chama o callback para voltar
            modifier = Modifier.align(Alignment.CenterHorizontally) // Centraliza o botão horizontalmente
        ) {
            Text("Voltar")
        }
    }
}