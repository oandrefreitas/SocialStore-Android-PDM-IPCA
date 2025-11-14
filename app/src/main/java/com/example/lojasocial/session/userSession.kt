package com.example.lojasocial.session

// Objeto que representa a sessão do utilizador
object UserSession {

    // Variáveis privadas para armazenar o ID do utilizador e o nível de acesso
    private var userId: String? = null
    private var accessLevel: Int? = null

    // Função para definir o `userId` e o `accessLevel` na sessão
    // Esta função é usada quando o utilizador inicia sessão com sucesso
    fun setUserSession(userId: String, accessLevel: Int) {
        this.userId = userId // Define o ID do utilizador na sessão
        this.accessLevel = accessLevel // Define o nível de acesso do utilizador na sessão
    }

    // Função para obter o `userId` da sessão
    // Retorna o ID do utilizador atual ou `null` caso não exista sessão
    fun getUserId(): String? {
        return userId
    }

    // Função para obter o `accessLevel` da sessão
    // Retorna o nível de acesso do utilizador atual ou `null` caso não exista sessão
    fun getAccessLevel(): Int? {
        return accessLevel
    }

    // Função para limpar os dados da sessão
    // É utilizada, por exemplo, durante o logout
    fun clearSession() {
        userId = null // Remove o ID do utilizador da sessão
        accessLevel = null // Remove o nível de acesso da sessão
    }
}