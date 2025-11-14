package com.example.lojasocial.ui.visita

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lojasocial.models.Beneficiario
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun VisitaView(
    viewModel: VisitaViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.value

    // Verifica se deve exibir a lista de visitas ou os beneficiários
    if (state.showVisitasScreen) {
        // Mostra o ecrã de lista de visitas
        VisitaListScreen(
            visitas = state.visitas,
            onBack = { viewModel.dismissVisitasScreen() } // Callback para fechar a lista de visitas
        )
    } else {
        // Layout principal para pesquisa e exibição dos beneficiários
        Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
            // Componente de pesquisa
            SearchRowView(onSearch = { query -> viewModel.searchBeneficiarios(query) })
            Spacer(modifier = Modifier.height(16.dp))

            // Exibição do estado de carregamento, erro ou lista de beneficiários
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally)) // Indicador de carregamento
            } else if (state.errorMessage != null) {
                // Mostra uma mensagem de erro se necessário
                Text(
                    text = "Erro: ${state.errorMessage}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                // Lista de beneficiários
                LazyColumn {
                    items(state.beneficiarios) { beneficiario ->
                        BeneficiarioRow(
                            beneficiario = beneficiario,
                            onClick = { viewModel.onBeneficiarioClick(beneficiario) } // Callback para selecionar beneficiário
                        )
                    }
                }
            }
        }

        // Exibe o diálogo de registo de visitas, se um beneficiário estiver selecionado
        state.selectedBeneficiario?.let { beneficiario ->
            VisitaDialog(
                beneficiario = beneficiario,
                onSave = { notas -> viewModel.registerVisita(notas) }, // Regista a visita
                onDismiss = { viewModel.dismissVisitaDialog() }, // Fecha o diálogo
                onShowVisitas = { viewModel.loadVisitasByBeneficiario(it.id) } // Carrega as visitas do beneficiário
            )
        }

        // Carrega os beneficiários ao iniciar o ecrã
        LaunchedEffect(Unit) { viewModel.loadBeneficiarios()
        }
    }
}

@Composable
fun BeneficiarioRow(beneficiario: Beneficiario, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth() // Ocupar toda a largura
            .padding(vertical = 4.dp) // Margem vertical
            .clickable { onClick() }, // Clique para interagir com o beneficiário
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier.padding(16.dp), // Preenchimento interno do cartão
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Informações detalhadas do beneficiário
            Column(modifier = Modifier.weight(1f)) {
                Row {
                    Text(
                        text = "Nome: ",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), // Negrito
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "${beneficiario.primeiroNome} ${beneficiario.sobrenome}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Row {
                    Text(
                        text = "Telefone: ",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = beneficiario.telemovel,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Row {
                    Text(
                        text = "Família: ",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = beneficiario.familia?.toString() ?: "N/A",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Row {
                    Text(
                        text = "Crianças: ",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = beneficiario.criancas?.toString() ?: "N/A",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Row {
                    Text(
                        text = "País: ",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = beneficiario.nacionalidade,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}