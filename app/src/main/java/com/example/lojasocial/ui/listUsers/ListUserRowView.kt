package com.example.lojasocial.ui.listUsers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lojasocial.models.User

@Composable
fun ListUserRow(
    user: User,
    onUpdateAccessLevel: (Int) -> Unit, // Callback para alterar o nível de acesso
    onToggleActiveStatus: (Boolean) -> Unit // Callback para ativar/desativar o utilizador
) {
    // Estado para gerir a expansão do menu dropdown
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // Cartão para exibir os detalhes do utilizador
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), // Espaçamento externo
        shape = RoundedCornerShape(8.dp), // Bordas arredondadas
        elevation = CardDefaults.cardElevation(4.dp) // Elevação do cartão
    ) {
        // Layout em linha para organizar os elementos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp), // Espaçamento interno
            verticalAlignment = Alignment.CenterVertically // Alinha os itens verticalmente ao centro
        ) {
            // Ícone e detalhes do utilizador
            Row(
                verticalAlignment = Alignment.CenterVertically, // Alinha verticalmente os itens ao centro
                modifier = Modifier.weight(1f) // Ocupa o espaço restante na linha
            ) {
                Icon(
                    imageVector = Icons.Default.Person, // Ícone do utilizador
                    contentDescription = "Ícone do Utilizador",
                    modifier = Modifier
                        .size(40.dp) // Define o tamanho do ícone
                        .background(
                            color = if (user.isActive) MaterialTheme.colorScheme.primary else Color.Gray, // Cor depende do estado ativo
                            shape = RoundedCornerShape(50) // Ícone arredondado
                        )
                        .padding(8.dp), // Espaçamento interno do ícone
                    tint = Color.White // Cor do ícone
                )
                Spacer(modifier = Modifier.width(16.dp)) // Espaçamento entre ícone e texto

                Column {
                    // Nome do utilizador
                    Text(
                        text = user.name,
                        fontWeight = FontWeight.Bold, // Texto em negrito
                        fontSize = 18.sp, // Tamanho da fonte
                        color = MaterialTheme.colorScheme.onSurface // Cor do texto
                    )
                    Spacer(modifier = Modifier.height(4.dp)) // Espaçamento entre textos
                    // Nível de acesso do utilizador
                    Text(
                        text = "Nível: ${user.accessLevel}",
                        fontSize = 14.sp, // Tamanho da fonte
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Cor do texto
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp)) // Espaçamento entre coluna de detalhes e menu

            // Menu Dropdown para alterar o nível de acesso
            Box(modifier = Modifier.wrapContentSize(Alignment.Center)) {
                Button(
                    onClick = { isDropdownExpanded = true }, // Expande o menu ao clicar
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary // Cor do botão
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp) // Preenchimento interno
                ) {
                    Text("Alterar Nível", fontSize = 12.sp) // Texto do botão
                }

                DropdownMenu(
                    expanded = isDropdownExpanded, // Controla se o menu está visível
                    onDismissRequest = { isDropdownExpanded = false } // Fecha o menu ao clicar fora
                ) {
                    DropdownMenuItem(
                        onClick = {
                            onUpdateAccessLevel(1) // Define o nível para Administrador
                            isDropdownExpanded = false
                        },
                        text = { Text("Administrador") }
                    )
                    DropdownMenuItem(
                        onClick = {
                            onUpdateAccessLevel(2) // Define o nível para Voluntário
                            isDropdownExpanded = false
                        },
                        text = { Text("Voluntário") }
                    )
                    DropdownMenuItem(
                        onClick = {
                            onUpdateAccessLevel(3) // Define o nível para Entidade
                            isDropdownExpanded = false
                        },
                        text = { Text("Entidade") }
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp)) // Espaçamento entre menu e interruptor

            // Interruptor para ativar/desativar o utilizador
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, // Centraliza horizontalmente
                verticalArrangement = Arrangement.Top // Alinha na parte superior
            ) {
                Switch(
                    checked = user.isActive, // Estado do interruptor
                    onCheckedChange = { isChecked ->
                        onToggleActiveStatus(isChecked) // Atualiza o estado ativo do utilizador
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White, // Cor do polegar ativo
                        uncheckedThumbColor = Color.White, // Cor do polegar inativo
                        checkedTrackColor = MaterialTheme.colorScheme.primary, // Cor da pista ativa
                        uncheckedTrackColor = Color.Gray // Cor da pista inativa
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListUserRowPreview() {
    ListUserRow(
        user = User(
            id = "1",
            name = "João Silva",
            accessLevel = 2, // Exemplo de nível de acesso
            isActive = true // Exemplo de estado ativo
        ),
        onUpdateAccessLevel = { newLevel ->
            println("Nível de Acesso atualizado para $newLevel") // Simulação de atualização
        },
        onToggleActiveStatus = { isActive ->
            println("Utilizador está ativo: $isActive") // Simulação de mudança de estado
        }
    )
}