package com.example.lojasocial.ui.visita

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lojasocial.models.Beneficiario
import com.example.lojasocial.models.Visita
import com.example.lojasocial.repository.BeneficiarioRepository
import com.example.lojasocial.repository.VisitaRepository

// Classe de estado para gerir o estado da UI relacionado às visitas
data class VisitaState(
    val beneficiarios: List<Beneficiario> = emptyList(), // Lista de beneficiários carregados
    val visitas: List<Visita> = emptyList(), // Lista de visitas carregadas
    val isLoading: Boolean = false, // Indica se os dados estão a ser carregados
    val errorMessage: String? = null, // Mensagem de erro, caso ocorra
    val showVisitaDialog: Boolean = false, // Indica se o pop-up de registo de visita está visível
    val selectedBeneficiario: Beneficiario? = null, // Beneficiário selecionado
    val showVisitasScreen: Boolean = false // Indica se a lista de visitas deve ser exibida
)

// ViewModel responsável por gerir a lógica da interface de visitas
class VisitaViewModel : ViewModel() {

    var state = mutableStateOf(VisitaState())
        private set

    // Função para carregar a lista de beneficiários do repositório
    fun loadBeneficiarios() {
        state.value = state.value.copy(isLoading = true, errorMessage = null)
        BeneficiarioRepository.getAllBeneficiarios(
            onSuccess = { beneficiarios ->
                state.value = state.value.copy(
                    beneficiarios = beneficiarios, // Atualiza a lista de beneficiários
                    isLoading = false // Indica que o carregamento terminou
                )
            },
            onFailure = { error ->
                state.value = state.value.copy(
                    isLoading = false,
                    errorMessage = error // Define a mensagem de erro
                )
            }
        )
    }

    // Função para filtrar beneficiários com base numa consulta
    fun searchBeneficiarios(query: String) {
        if (query.isBlank()) {
            loadBeneficiarios() // Se a consulta estiver vazia, recarrega todos os beneficiários
            return
        }
        state.value = state.value.copy(isLoading = true, errorMessage = null)
        BeneficiarioRepository.getAllBeneficiarios(
            onSuccess = { beneficiarios ->
                // Filtra os beneficiários pelo nome ou apelido
                val filtered = beneficiarios.filter {
                    it.primeiroNome.contains(query, ignoreCase = true) ||
                            it.sobrenome.contains(query, ignoreCase = true)
                }
                state.value = state.value.copy(
                    beneficiarios = filtered,
                    isLoading = false
                )
            },
            onFailure = { error ->
                state.value = state.value.copy(
                    isLoading = false,
                    errorMessage = error // Define a mensagem de erro
                )
            }
        )
    }

    // Função para exibir o pop-up de registo de visita
    fun onBeneficiarioClick(beneficiario: Beneficiario) {
        state.value = state.value.copy(
            showVisitaDialog = true, // Mostra o diálogo
            selectedBeneficiario = beneficiario // Define o beneficiário selecionado
        )
    }

    // Função para registar uma visita no Firestore
    fun registerVisita(notas: String) {
        val beneficiario = state.value.selectedBeneficiario ?: return
        val visita = Visita(
            notas = notas,
            data = System.currentTimeMillis(), // Define a data atual
            beneficiarioId = beneficiario.id
        )
        VisitaRepository.addVisita(
            visita = visita,
            onSuccess = {
                dismissVisitaDialog() // Fecha o diálogo após o sucesso
            },
            onFailure = { error ->
                state.value = state.value.copy(errorMessage = error) // Regista a mensagem de erro
            }
        )
    }

    // Fecha o pop-up de registo de visita
    fun dismissVisitaDialog() {
        state.value = state.value.copy(
            showVisitaDialog = false,
            selectedBeneficiario = null
        )
    }

    // Carrega as visitas associadas a um beneficiário específico
    fun loadVisitasByBeneficiario(beneficiarioId: String) {
        state.value = state.value.copy(isLoading = true, errorMessage = null)
        VisitaRepository.getVisitasByBeneficiarioId(
            beneficiarioId = beneficiarioId,
            onSuccess = { visitas ->
                state.value = state.value.copy(
                    visitas = visitas, // Atualiza a lista de visitas
                    isLoading = false,
                    showVisitasScreen = true // Mostra a lista de visitas
                )
            },
            onFailure = { error ->
                state.value = state.value.copy(
                    isLoading = false,
                    errorMessage = error // Regista a mensagem de erro
                )
            }
        )
    }

    // Fecha a tela de lista de visitas
    fun dismissVisitasScreen() {
        state.value = state.value.copy(showVisitasScreen = false)
    }
}