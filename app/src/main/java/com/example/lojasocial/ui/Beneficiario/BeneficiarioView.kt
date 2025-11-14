package com.example.lojasocial.ui.Beneficiario

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lojasocial.models.Beneficiario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeneficiarioView(
    onRegisterSuccess: () -> Unit, // Callback para quando o registo for bem-sucedido
    viewModel: BeneficiarioViewModel = viewModel(), // ViewModel que gere o estado da UI
    modifier: Modifier = Modifier // Modificador padrão para personalização do layout
) {
    val state = viewModel.state.value // Obtém o estado atual do ViewModel
    var expanded by remember { mutableStateOf(false) } // Estado para controlar o dropdown
    var filterText by remember { mutableStateOf("") } // Texto para filtrar os países

    // Verifique se a lista de nacionalidades não está vazia
    LaunchedEffect(state.nationalities) {
        // Adicionar um log ou breakpoint aqui
        println("Nationalities: ${state.nationalities}")
    }

    Column(
        modifier = modifier
            .fillMaxSize() // A coluna ocupa o tamanho total do ecrã
            .padding(16.dp), // Adiciona uma margem ao redor da coluna
        verticalArrangement = Arrangement.Top, // Alinha os elementos ao topo
        horizontalAlignment = Alignment.Start // Alinha os elementos horizontalmente à esquerda
    ) {

        Spacer(modifier = Modifier.height(16.dp)) // Espaçamento inicial

        // Campo de entrada para o primeiro nome
        OutlinedTextField(
            value = state.beneficiario.primeiroNome,
            onValueChange = { viewModel.onPrimeiroNomeChange(it) },
            label = { Text("Primeiro Nome") },
            modifier = Modifier.fillMaxWidth() // O campo ocupa a largura total
        )

        Spacer(modifier = Modifier.height(8.dp)) // Espaçamento entre os campos

        // Campo de entrada para o sobrenome
        OutlinedTextField(
            value = state.beneficiario.sobrenome,
            onValueChange = { viewModel.onSobrenomeChange(it) },
            label = { Text("Sobrenome") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo de entrada para o telefone
        OutlinedTextField(
            value = state.beneficiario.telemovel,
            onValueChange = { viewModel.onTelemovelChange(it) },
            label = { Text("Telefone") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone) // Define o teclado para telefone
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo de entrada para a referência
        OutlinedTextField(
            value = state.beneficiario.referencia,
            onValueChange = { viewModel.onReferenciaChange(it) },
            label = { Text("Referência") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo de entrada para a quantidade de membros da família
        OutlinedTextField(
            value = state.beneficiario.familia?.toString() ?: "",
            onValueChange = { viewModel.onFamiliaChange(it) },
            label = { Text("Família") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Define o teclado para números
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo de entrada para a quantidade de crianças
        OutlinedTextField(
            value = state.beneficiario.criancas?.toString() ?: "",
            onValueChange = { viewModel.onCriancasChange(it) },
            label = { Text("Crianças") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown com filtro para o país
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                if (state.beneficiario.nacionalidade.isEmpty()) {
                    expanded = !expanded
                }
            }
        ) {
            OutlinedTextField(
                value = if (state.beneficiario.nacionalidade.isNotEmpty()) {
                    state.beneficiario.nacionalidade
                } else {
                    filterText
                },
                onValueChange = { newText ->
                    if (state.beneficiario.nacionalidade.isEmpty()) {
                        filterText = newText
                    }
                },
                label = { Text("País") },
                trailingIcon = {
                    if (state.beneficiario.nacionalidade.isNotEmpty()) {
                        IconButton(onClick = {
                            viewModel.onNacionalidadeChange("") // Reseta a seleção
                            filterText = "" // Limpa o filtro
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Limpar seleção"
                            )
                        }
                    }
                },
                readOnly = state.beneficiario.nacionalidade.isNotEmpty(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            if (expanded) {
                val filteredCountries = state.nationalities.filter {
                    it.contains(filterText, ignoreCase = true)
                }

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    filteredCountries.forEach { country ->
                        DropdownMenuItem(
                            text = { Text(country) },
                            onClick = {
                                viewModel.onNacionalidadeChange(country)
                                filterText = ""
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão para registar o beneficiário
        Button(
            onClick = {
                if (state.beneficiario.nacionalidade.isEmpty()) {
                    viewModel.showError("Selecione um País válido.") // Exibe erro
                    return@Button
                }
                val beneficiario = Beneficiario(
                    primeiroNome = state.beneficiario.primeiroNome,
                    sobrenome = state.beneficiario.sobrenome,
                    telemovel = state.beneficiario.telemovel,
                    referencia = state.beneficiario.referencia,
                    familia = state.beneficiario.familia,
                    criancas = state.beneficiario.criancas,
                    nacionalidade = state.beneficiario.nacionalidade,
                    isActive = true
                )

                // Chama a função do ViewModel para registar o beneficiário
                viewModel.registerBeneficiario(beneficiario) {
                    onRegisterSuccess() // Callback chamado em caso de sucesso
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(50.dp), // Altura do botão
            shape = RoundedCornerShape(10.dp) // Cantos arredondados
        ) {
            Text("Registar Beneficiário")
        }

        // Mostra a mensagem de sucesso, se disponível
        state.successMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Mostra a mensagem de erro, se disponível
        state.errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Indicador de carregamento, caso esteja a processar
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}
