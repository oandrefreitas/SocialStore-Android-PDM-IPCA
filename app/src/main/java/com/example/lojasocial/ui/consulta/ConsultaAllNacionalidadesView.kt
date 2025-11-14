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
fun ConsultaAllNacionalidadesView(
    viewModel: ConsultaViewModel = viewModel(),
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Obter o estado atual do ViewModel
    val state = viewModel.state.value

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp) // Preenchimento em torno da coluna
    ) {
        // Botão "Voltar" no topo, alinhado à esquerda
        Button(
            onClick = onBack,
            modifier = Modifier.align(Alignment.Start), // Alinha o botão à esquerda
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), // Define o tamanho do botão
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) // Cor do botão
        ) {
            Text(text = "Voltar", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(16.dp)) // Espaçamento abaixo do botão

        // Mostrar indicador de carregamento, mensagem de erro ou a lista de Países
        if (state.isLoading) {
            // Indicador de carregamento
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        } else if (state.errorMessage != null) {
            // Mensagem de erro em caso de falha
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Erro: ${state.errorMessage}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            // Lista de Países em formato LazyColumn
            LazyColumn(
                modifier = Modifier.fillMaxWidth() // Ocupa a largura total disponível
            ) {
                // Iterar pelos Países e os seus totais
                items(state.nacionalidadesTotais.entries.toList()) { (nacionalidade, total) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth() // O cartão ocupa a largura total
                            .padding(vertical = 4.dp), // Espaçamento vertical entre os cartões
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary) // Cor de fundo do cartão
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth() // Ocupa a largura total
                                .padding(16.dp), // Preenchimento dentro do cartão
                            horizontalArrangement = Arrangement.SpaceBetween // Espaçamento entre os itens na linha
                        ) {
                            // Texto com o nome do País
                            Text(
                                text = nacionalidade,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), // Texto em negrito
                                color = MaterialTheme.colorScheme.onPrimary // Cor do texto
                            )
                            // Texto com o número total de registos para o País
                            Text(
                                text = "$total",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // Espaçamento entre a lista e o botão
    }

    // Carregar os dados de nacionalidades totais assim que o ecrã for apresentado
    LaunchedEffect(Unit) {
        viewModel.loadNacionalidadesTotais()
    }
}