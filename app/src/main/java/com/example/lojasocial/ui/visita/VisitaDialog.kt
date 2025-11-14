package com.example.lojasocial.ui.visita

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lojasocial.models.Beneficiario

@Composable
fun VisitaDialog(
    beneficiario: Beneficiario, // Beneficiário para o qual a visita será registada
    onSave: (String) -> Unit, // Callback para guardar as notas
    onDismiss: () -> Unit, // Callback para fechar o diálogo sem guardar
    onShowVisitas: (Beneficiario) -> Unit // Callback para mostrar todas as visitas do beneficiário
) {
    var notas by remember { mutableStateOf("") } // Estado local para armazenar as notas

    AlertDialog(
        onDismissRequest = { onDismiss() }, // Fecha o diálogo ao clicar fora
        confirmButton = {
            Button(onClick = { onSave(notas) }) { // Botão para guardar as notas
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) { // Botão para cancelar
                Text("Cancelar")
            }
        },
        title = { Text("Registar Visita") }, // Título do diálogo
        text = {
            Column {
                // Nome completo do beneficiário
                Text("Beneficiário: ${beneficiario.primeiroNome} ${beneficiario.sobrenome}")
                Spacer(modifier = Modifier.height(8.dp)) // Espaçamento entre os elementos

                // Campo de texto para introduzir notas
                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it },
                    label = { Text("Notas") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp)) // Espaçamento entre os elementos

                // Botão para ver todas as visitas do beneficiário
                Button(
                    onClick = { onShowVisitas(beneficiario) }, // Chama o callback com o beneficiário
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver Visitas")
                }
            }
        }
    )
}