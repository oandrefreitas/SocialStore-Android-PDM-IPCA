package com.example.lojasocial.ui.components

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager

@Composable
fun DismissKeyboard(
    content: @Composable () -> Unit // Função de composição que representa o conteúdo a ser exibido
) {
    // Obtém o contexto atual e o gerenciador de foco local
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // Envolve o conteúdo num Box que detecta toques fora de campos de entrada
    Box(
        modifier = Modifier
            .fillMaxSize() // Ocupa toda a área disponível
            .pointerInput(Unit) { // Adiciona deteção de gestos ao Box
                detectTapGestures(onTap = {
                    focusManager.clearFocus() // Remove o foco de qualquer campo de texto
                    hideKeyboard(context) // Esconde o teclado
                })
            }
    ) {
        content() // Exibe o conteúdo fornecido
    }
}

// Função para esconder o teclado virtual
fun hideKeyboard(context: Context) {
    // Obtém o InputMethodManager para manipular o teclado
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    // Esconde o teclado, independentemente de haver um campo de entrada ativo
    imm.hideSoftInputFromWindow(null, 0)
}