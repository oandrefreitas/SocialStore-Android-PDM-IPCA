package com.example.lojasocial.ui.consulta

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ConsultaAllVisitasView(
    viewModel: ConsultaViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onBack: () -> Unit // Callback para voltar ao ecrã anterior
) {
    val state = viewModel.state.value // Obtém o estado atual do ViewModel

    // Carregar as visitas por ano assim que o ecrã for apresentado
    LaunchedEffect(Unit) {
        viewModel.loadVisitasPorAno()
    }

    Column(
        modifier = modifier
            .fillMaxSize() // Preenche a altura e largura totais do ecrã
            .padding(16.dp) // Adiciona margem ao redor
    ) {
        // Botão "Voltar" no topo, alinhado à esquerda
        Button(
            onClick = onBack,
            modifier = Modifier.align(Alignment.Start), // Alinha o botão à esquerda
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), // Define o tamanho do botão
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) // Define a cor do botão
        ) {
            Text(text = "Voltar", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(16.dp)) // Espaçamento abaixo do botão

        // Verifica se está a carregar
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        // Verifica se ocorreu um erro
        else if (state.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Erro: ${state.errorMessage}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        // Verifica se não existem registos de visitas
        else if (state.visitasPorAno.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Nenhum registo de visitas encontrado.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        // Mostra a lista de visitas agrupadas por ano
        else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth() // A lista ocupa a largura total
            ) {
                items(state.visitasPorAno.entries.toList()) { (ano, totalVisitas) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth() // O cartão ocupa a largura total
                            .padding(vertical = 4.dp), // Espaçamento entre os cartões
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary) // Define a cor do cartão
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth() // A linha ocupa a largura total
                                .padding(16.dp), // Margem interna do cartão
                            horizontalArrangement = Arrangement.SpaceBetween // Espaçamento entre os elementos da linha
                        ) {
                            // Mostra o ano em negrito
                            Text(
                                text = "Total de Visitas em $ano:",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            // Mostra o total de visitas
                            Text(
                                text = "$totalVisitas",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}