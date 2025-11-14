package com.example.lojasocial.ui.visita

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchRowView(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit // Callback acionado sempre que o texto no campo de pesquisa mudar
) {
    // Variável de estado para controlar o texto inserido no campo de pesquisa
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    Row(
        verticalAlignment = Alignment.CenterVertically, // Alinha os elementos verticalmente ao centro
        modifier = modifier
            .fillMaxWidth() // O Row ocupa toda a largura disponível
            .padding(8.dp) // Espaçamento externo ao redor do Row
    ) {
        OutlinedTextField(
            value = searchQuery, // Valor atual do campo de texto
            onValueChange = {
                searchQuery = it // Atualiza a variável de estado com o novo texto
                onSearch(it.text) // Chama a função de pesquisa com o texto atual
            },
            placeholder = { Text("Pesquisar...") }, // Texto exibido quando o campo está vazio
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search, // Ícone de pesquisa
                    contentDescription = "Pesquisar", // Descrição para acessibilidade
                    tint = Color.Gray // Cor do ícone
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.Black, // Cor do texto quando o campo está em foco
                unfocusedTextColor = Color.Black, // Cor do texto quando o campo não está em foco
                focusedPlaceholderColor = Color.Gray, // Cor do placeholder quando o campo está em foco
                unfocusedPlaceholderColor = Color.Gray, // Cor do placeholder quando o campo não está em foco
                focusedBorderColor = Color.Black, // Cor da borda quando o campo está em foco
                unfocusedBorderColor = Color.Gray, // Cor da borda quando o campo não está em foco
                cursorColor = Color.Black // Cor do cursor
            ),
            singleLine = true, // Garante que o campo de texto será de uma única linha
            modifier = Modifier
                .weight(1f) // Faz com que o campo de texto ocupe o espaço restante do Row
                .height(56.dp), // Altura do campo de texto
            shape = RoundedCornerShape(16.dp) // Define os cantos arredondados do campo
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchRowViewPreview() {
    // Exemplo de pré-visualização da barra de pesquisa
    SearchRowView(onSearch = { query ->
        println("Pesquisar por: $query") // Simula a execução de uma pesquisa
    })
}