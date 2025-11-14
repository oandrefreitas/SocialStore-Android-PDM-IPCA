package com.example.lojasocial.ui.cashFlow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.models.CashTransaction
import com.example.lojasocial.models.TransactionType
import com.example.lojasocial.repository.CashRepository
import com.example.lojasocial.session.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Representa o estado da UI para o fluxo de caixa.
 */
data class CashFlowUiState(
    val isLoading: Boolean = false, // Indica se os dados estão a ser carregados
    val balance: Double = 0.0, // Saldo atual
    val transactions: List<CashTransaction> = emptyList(), // Lista de transações carregadas
    val totalTransactions: Int = 0, // Total de transações
    val startDate: Long? = null, // Data inicial do intervalo
    val endDate: Long? = null, // Data final do intervalo
    val errorMessage: String? = null // Mensagem de erro, caso exista
)

/**
 * ViewModel para gerir o estado e as operações relacionadas ao fluxo de caixa.
 */
class CashFlowViewModel : ViewModel() {

    // Estado interno da UI
    private val _uiState = MutableStateFlow(CashFlowUiState())
    val uiState: StateFlow<CashFlowUiState> = _uiState

    // Lista de todas as transações carregadas
    private var allTransactions: List<CashTransaction> = emptyList()

    // Inicialização: carrega o saldo atual e as transações de hoje
    init {
        loadCurrentBalance()
        loadTransactionsToday()
    }

    /**
     * Carrega o saldo atual do fluxo de caixa.
     */
    fun loadCurrentBalance() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            CashRepository.getCurrentBalance(
                onSuccess = { balance ->
                    _uiState.value = _uiState.value.copy(isLoading = false, balance = balance)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error)
                }
            )
        }
    }

    /**
     * Carrega as transações realizadas no dia atual.
     */
    fun loadTransactionsToday() {
        val todayTimestamp = System.currentTimeMillis() // Obtém o timestamp atual

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            CashRepository.getTodayTransactions(
                dayTimestamp = todayTimestamp, // Passa o timestamp do dia atual
                onSuccess = { transactions ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        transactions = transactions
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error)
                }
            )
        }
    }

    /**
     * Carrega as transações num intervalo específico de datas.
     *
     * @param startDate Data inicial do intervalo.
     * @param endDate Data final do intervalo.
     */
    fun loadTransactionsByDateRange(startDate: Long, endDate: Long) {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            CashRepository.getTransactionsByDateRange(
                startDate = startDate,
                endDate = endDate,
                onSuccess = { transactions ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        transactions = transactions,
                        startDate = startDate,
                        endDate = endDate
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error)
                }
            )
        }
    }

    /**
     * Adiciona uma nova transação ao fluxo de caixa.
     *
     * @param amount Valor da transação.
     * @param description Descrição da transação.
     * @param type Tipo da transação (Entrada, Saída, Ajuste).
     */
    fun addTransaction(amount: Double, description: String, type: TransactionType) {
        val creatorUid = UserSession.getUserId() // Obtém o ID do utilizador autenticado

        // Verifica se o utilizador está autenticado
        if (creatorUid == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "Erro: Usuário não autenticado.")
            return
        }

        // Cria uma nova transação
        val transaction = CashTransaction(
            id = "", // O ID será gerado automaticamente no Firestore
            amount = amount,
            description = description,
            type = type,
            timestamp = System.currentTimeMillis(), // Marca a transação com o timestamp atual
            creatorUid = creatorUid
        )

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            CashRepository.addTransaction(
                transaction = transaction,
                onSuccess = {
                    loadTransactionsToday() // Recarrega as transações do dia
                    loadCurrentBalance() // Atualiza o saldo
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error)
                }
            )
        }
    }
}