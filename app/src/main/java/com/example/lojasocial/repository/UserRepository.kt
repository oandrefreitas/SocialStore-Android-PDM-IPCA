package com.example.lojasocial.repository

import android.util.Log
import com.example.lojasocial.models.User
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit
import com.example.lojasocial.TAG
import com.google.firebase.auth.FirebaseUser

object UserRepository {

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    // Adiciona utilizador com e-mail e password
    fun registerWithEmail(
        email: String,
        password: String,
        name: String,
        phone: String? = null,
        accessLevel: Int = 2,
        isActive: Boolean = true,
        onAddSuccess: () -> Unit,
        onAddFailure: (String) -> Unit
    ) {
        // Cria utilizador com autenticação de e-mail e password
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid
                if (userId != null) {
                    // Cria o objeto utilizador com os detalhes fornecidos
                    val user = User(
                        id = userId,
                        name = name,
                        email = email,
                        phone = phone,
                        accessLevel = accessLevel,
                        isActive = isActive
                    )
                    // Guarda o utilizador na base de dados
                    saveUser(user, onAddSuccess, onAddFailure)
                } else {
                    Log.e(TAG, "Erro ao obter o UID do utilizador.")
                    onAddFailure("Erro ao obter o UID do utilizador.")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Erro ao criar utilizador no Firebase Authentication", e)
                onAddFailure(e.message ?: "Erro desconhecido.")
            }
    }

    // Inicia o processo de autenticação por telefone
    fun registerWithPhone(
        phoneNumber: String,
        onCodeSent: (String, PhoneAuthProvider.ForceResendingToken) -> Unit,
        onVerificationCompleted: (PhoneAuthCredential) -> Unit,
        onVerificationFailed: (String) -> Unit
    ) {
        // Configura as opções de autenticação por telefone
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d(TAG, "Verificação concluída automaticamente com sucesso")
                    onVerificationCompleted(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e(TAG, "Falha na verificação: ${e.message}")
                    onVerificationFailed(e.message ?: "Erro desconhecido na verificação")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    Log.d(TAG, "Código de verificação enviado com ID: $verificationId")
                    onCodeSent(verificationId, token)
                }
            })
            .build()

        // Inicia o processo de verificação
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // Conclui o registo do utilizador após a verificação do telefone e guarda os detalhes
    fun saveUserWithPhoneCredential(
        name: String,
        phone: String,
        credential: PhoneAuthCredential,
        accessLevel: Int = 2,
        isActive: Boolean = true,
        onAddSuccess: () -> Unit,
        onAddFailure: (String) -> Unit
    ) {
        // Realiza o login com as credenciais de telefone verificadas
        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid
                if (userId != null) {
                    // Cria o objeto utilizador com os detalhes fornecidos
                    val user = User(
                        id = userId,
                        name = name,
                        phone = phone,
                        email = null, // Não é necessário e-mail para registo por telefone
                        accessLevel = accessLevel,
                        isActive = isActive
                    )
                    // Guarda o utilizador na base de dados
                    saveUser(user, onAddSuccess, onAddFailure)
                } else {
                    Log.e(TAG, "Erro ao obter o UID do utilizador.")
                    onAddFailure("Erro ao obter o UID do utilizador.")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Erro ao fazer login com credencial de telefone", e)
                onAddFailure(e.message ?: "Erro desconhecido.")
            }
    }

    // Guarda as informações do utilizador na base de dados Firestore
    private fun saveUser(
        user: User,
        onAddSuccess: () -> Unit,
        onAddFailure: (String) -> Unit
    ) {
        db.collection("Users").document(user.id)
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "Utilizador gravado com sucesso no Firestore!")
                onAddSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Erro ao gravar utilizador no Firestore", e)
                onAddFailure(e.message ?: "Erro desconhecido ao gravar no Firestore.")
            }
    }

    // Obtém os detalhes de um utilizador específico
    fun getUserDetails(
        userId: String,
        onSuccess: (User) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("Users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(User::class.java)
                    if (user != null) {
                        if (user.isActive) {
                            onSuccess(user)
                        } else {
                            onFailure("Utilizador está inativo. Contacte o administrador.")
                        }
                    } else {
                        onFailure("Erro ao converter os dados do utilizador.")
                    }
                } else {
                    onFailure("Utilizador não encontrado.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "Erro ao procurar detalhes do utilizador", e)
                onFailure(e.message ?: "Erro desconhecido ao procurar detalhes do utilizador.")
            }
    }

    // Atualiza os detalhes de um utilizador, incluindo o nome e a password
    fun updateUserDetails(
        updatedName: String,
        newPassword: String? = null,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val currentUser = auth.currentUser
        val userId = currentUser?.uid

        if (currentUser == null) {
            onFailure("Erro: Utilizador não autenticado.")
            return
        }

        Log.d("UserRepository", "Atualizando nome do utilizador com ID: $userId para: $updatedName")

        applyUpdates(currentUser, updatedName, newPassword, userId, onSuccess, onFailure)
    }

    // Aplica as atualizações no Firestore e no Firebase Authentication
    private fun applyUpdates(
        currentUser: FirebaseUser,
        updatedName: String,
        newPassword: String?,
        userId: String?,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (userId == null) {
            onFailure("Erro: ID do utilizador não encontrado.")
            return
        }

        val updates = mapOf(
            "name" to updatedName
        )

        db.collection("Users").document(userId)
            .update(updates)
            .addOnSuccessListener {
                Log.d("UserRepository", "Nome atualizado no Firestore com sucesso: $updatedName")
                updatePassword(currentUser, newPassword, onSuccess, onFailure)
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "Erro ao atualizar Firestore: ${e.message}", e)
                onFailure("Erro ao atualizar o nome: ${e.message}")
            }
    }

    // Atualiza a password no Firebase Authentication
    private fun updatePassword(
        currentUser: FirebaseUser,
        newPassword: String?,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (!newPassword.isNullOrEmpty()) {
            currentUser.updatePassword(newPassword)
                .addOnSuccessListener {
                    Log.d("UserRepository", "Password atualizada no Firebase Auth.")
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    Log.e("UserRepository", "Erro ao atualizar password: ${e.message}", e)
                    onFailure("Erro ao atualizar password: ${e.message}")
                }
        } else {
            onSuccess()
        }
    }

    // Atualiza o estado ativo/inativo de um utilizador
    fun updateUserStatus(
        userId: String,
        isActive: Boolean,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("Users").document(userId)
            .update("isActive", isActive)
            .addOnSuccessListener {
                Log.d("UserRepository", "Estado do utilizador atualizado com sucesso.")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "Erro ao atualizar o estado do utilizador", e)
                onFailure(e.message ?: "Erro desconhecido ao atualizar o estado do utilizador.")
            }
    }

    // Atualiza o nível de acesso de um utilizador
    fun updateUserAccessLevel(
        userId: String,
        newAccessLevel: Int,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("Users").document(userId)
            .update("accessLevel", newAccessLevel)
            .addOnSuccessListener {
                Log.d("UserRepository", "Nível de acesso do utilizador atualizado com sucesso.")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "Erro ao atualizar o nível de acesso do utilizador", e)
                onFailure(e.message ?: "Erro desconhecido ao atualizar o nível de acesso.")
            }
    }

    // Obtém todos os utilizadores registados
    fun getAllUsers(
        onSuccess: (List<User>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("Users")
            .get()
            .addOnSuccessListener { result ->
                val users = result.documents.mapNotNull { it.toObject(User::class.java) }
                onSuccess(users)
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "Erro ao buscar todos os utilizadores", e)
                onFailure(e.message ?: "Erro desconhecido ao buscar utilizadores")
            }
    }
}