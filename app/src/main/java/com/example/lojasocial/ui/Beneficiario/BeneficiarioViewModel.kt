package com.example.lojasocial.ui.Beneficiario

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.lojasocial.models.Beneficiario
import com.example.lojasocial.repository.BeneficiarioRepository
import java.util.Locale

// Estado para a interface do beneficiário
data class BeneficiarioState(
    val beneficiario: Beneficiario = Beneficiario(), // Beneficiário atualmente sendo editado ou criado
    val isLoading: Boolean = false, // Indicador de carregamento
    val successMessage: String? = null, // Mensagem de sucesso, caso o registo seja bem-sucedido
    val errorMessage: String? = null, // Mensagem de erro, caso ocorra algum problema
    val nationalities: List<String> = emptyList() // Lista de Países disponíveis
)

class BeneficiarioViewModel : ViewModel() {

    // Estado atual do ViewModel
    var state = mutableStateOf(BeneficiarioState())
        private set // Apenas o ViewModel pode modificar este estado

    init {
        loadNationalities() // Carrega os Países ao iniciar
    }

    // Função para carregar a lista de Países
    private fun loadNationalities() {
        val localePt = Locale("pt") // Define o Locale como português
        val countries = Locale.getISOCountries().map { countryCode ->
            val locale = Locale("", countryCode)
            locale.getDisplayCountry(localePt) // Obtém o nome do país em português
        }.sorted()

        state.value = state.value.copy(nationalities = countries)
    }

    // Funções para atualizar os campos do beneficiário
    fun onPrimeiroNomeChange(newValue: String) {
        // Atualiza o campo do primeiro nome no estado atual
        state.value = state.value.copy(beneficiario = state.value.beneficiario.copy(primeiroNome = newValue))
    }

    fun onSobrenomeChange(newValue: String) {
        // Atualiza o campo do sobrenome no estado atual
        state.value = state.value.copy(beneficiario = state.value.beneficiario.copy(sobrenome = newValue))
    }

    fun onTelemovelChange(newValue: String) {
        // Atualiza o campo do telemóvel no estado atual
        state.value = state.value.copy(beneficiario = state.value.beneficiario.copy(telemovel = newValue))
    }

    fun onReferenciaChange(newValue: String) {
        // Atualiza o campo de referência no estado atual
        state.value = state.value.copy(beneficiario = state.value.beneficiario.copy(referencia = newValue))
    }

    fun onFamiliaChange(newValue: String) {
        // Atualiza o campo da quantidade de membros da família no estado atual
        state.value = state.value.copy(beneficiario = state.value.beneficiario.copy(familia = newValue.toIntOrNull()))
    }

    fun onCriancasChange(newValue: String) {
        // Atualiza o campo da quantidade de crianças no estado atual
        state.value = state.value.copy(beneficiario = state.value.beneficiario.copy(criancas = newValue.toIntOrNull()))
    }

    fun onNacionalidadeChange(newValue: String) {
        state.value = state.value.copy(beneficiario = state.value.beneficiario.copy(nacionalidade = newValue))
    }

    // Função para exibir mensagens de erro
    fun showError(message: String) {
        state.value = state.value.copy(errorMessage = message)
    }

    // Função para registar o beneficiário na base de dados Firestore
    fun registerBeneficiario(beneficiario: Beneficiario, onRegisterSuccess: () -> Unit) {
        // Atualiza o estado para indicar que o registo está em andamento
        state.value = state.value.copy(isLoading = true)

        // Chama a função do repositório para adicionar o beneficiário
        BeneficiarioRepository.addBeneficiario(
            beneficiario = beneficiario,
            onSuccess = {
                // Atualiza o estado com a mensagem de sucesso e desativa o indicador de carregamento
                state.value = state.value.copy(
                    isLoading = false,
                    successMessage = "Beneficiário registado com sucesso!"
                )
                onRegisterSuccess()
            },
            onFailure = { errorMessage ->
                // Atualiza o estado com a mensagem de erro e desativa o indicador de carregamento
                state.value = state.value.copy(isLoading = false, errorMessage = errorMessage)
            }
        )
    }
}
