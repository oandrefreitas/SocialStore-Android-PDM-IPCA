package com.example.lojasocial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lojasocial.repository.UserRepository
import com.example.lojasocial.session.UserSession
import com.example.lojasocial.ui.Beneficiario.BeneficiarioView
import com.example.lojasocial.ui.cashFlow.CashFlowView
import com.example.lojasocial.ui.components.BottomBar
import com.example.lojasocial.ui.components.TopBar
import com.example.lojasocial.ui.consulta.ConsultaAllNacionalidadesView
import com.example.lojasocial.ui.consulta.ConsultaAllVisitasView
import com.example.lojasocial.ui.consulta.ConsultaView
import com.example.lojasocial.ui.home.HomeView
import com.example.lojasocial.ui.listUsers.ListUsersView
import com.example.lojasocial.ui.login.LoginView
import com.example.lojasocial.ui.profile.EditProfileView
import com.example.lojasocial.ui.register.RegisterView
import com.example.lojasocial.ui.theme.LojaSocialTheme
import com.example.lojasocial.ui.visita.VisitaView
import com.example.lojasocial.ui.welcomeScreen.WelcomeScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

const val TAG = "lojasocial"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuração para forçar o modo claro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            LojaSocialTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        val currentBackStackEntry = navController.currentBackStackEntryAsState()
                        val currentRoute = currentBackStackEntry.value?.destination?.route

                        // Mostra a TopBar apenas em páginas diferentes de "login" e "register"
                        if (currentRoute !in listOf(
                                Screen.Login.route,
                                Screen.Register.route,
                                Screen.Welcome.route
                            )
                        ) {
                            TopBar(
                                title = when (currentRoute) {
                                    Screen.Home.route -> "Loja Social"
                                    Screen.EditProfile.route -> "Editar Perfil"
                                    Screen.ListUsers.route -> "Gerir Voluntários"
                                    Screen.CashFlow.route -> "Fluxo de Caixa"
                                    Screen.Beneficiario.route -> "Registar Beneficiário"
                                    Screen.Visita.route -> "Registar Visitas"
                                    Screen.Consulta.route -> "Consultar Dados"
                                    Screen.AllVisitas.route -> "Visitas Por Ano"
                                    Screen.AllNacionalidades.route -> "Países Gerais"
                                    else -> "Loja Social"
                                },
                                onHomeClick = { navController.navigate(Screen.Home.route) },
                                onLogoffClick = {
                                    Firebase.auth.signOut()
                                    UserSession.clearSession()
                                    navController.navigate(Screen.Welcome.route) {
                                        popUpTo(Screen.Welcome.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                    },
                    bottomBar = {
                        BottomBar() // Adiciona a BottomBar para todos os ecrãs
                    }
                ) { innerPadding ->
                    NavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        startDestination = Screen.Welcome.route
                    ) {
                        composable(Screen.Welcome.route) {
                            WelcomeScreen(
                                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                            )
                        }

                        composable(Screen.Register.route) {
                            RegisterView(onRegisterSuccess = {
                                navController.navigate(Screen.Home.route) // Redireciona para a Home após registo
                            })
                        }

                        composable(Screen.Login.route) {
                            LoginView(onLoginSuccess = {
                                navController.navigate(Screen.Home.route)
                            })
                        }

                        composable(Screen.Home.route) {
                            HomeView(
                                onEditProfileClicked = { navController.navigate(Screen.EditProfile.route) },
                                onVolunteerManagementClicked = { navController.navigate(Screen.ListUsers.route) },
                                onCashFlowClicked = { navController.navigate(Screen.CashFlow.route) },
                                onRegisterBeneficiaryClicked = { navController.navigate(Screen.Beneficiario.route) },
                                onVisitaClicked = { navController.navigate(Screen.Visita.route) },
                                onConsultaClicked = { navController.navigate(Screen.Consulta.route) }
                            )
                        }

                        composable(Screen.EditProfile.route) {
                            val targetUserId = UserSession.getUserId() // Obtém o ID do utilizador da sessão
                            EditProfileView(
                                targetUserId = targetUserId,
                                onProfileUpdated = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(Screen.ListUsers.route) { ListUsersView() }
                        composable(Screen.CashFlow.route) { CashFlowView() }

                        composable(Screen.Beneficiario.route) {
                            BeneficiarioView(
                                onRegisterSuccess = {
                                    navController.navigate(Screen.Visita.route)
                                }
                            )
                        }
                        composable(Screen.Visita.route) {
                            VisitaView()
                        }
                        composable(Screen.Consulta.route) {
                            ConsultaView(
                                onNavigateToAllVisitas = { navController.navigate(Screen.AllVisitas.route) },
                                onNavigateToAllNacionalidades = { navController.navigate(Screen.AllNacionalidades.route) }
                            )
                        }
                        composable(Screen.AllVisitas.route) {
                            ConsultaAllVisitasView(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.AllNacionalidades.route) {
                            ConsultaAllNacionalidadesView(
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }

                    // Verifica o utilizador autenticado no Firebase ao iniciar a aplicação
                    LaunchedEffect(Unit) {
                        val auth = Firebase.auth
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            UserRepository.getUserDetails(
                                userId = currentUser.uid,
                                onSuccess = { user ->
                                    UserSession.setUserSession(user.id, user.accessLevel)
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Welcome.route) { inclusive = true }
                                    }
                                },
                                onFailure = {
                                    navController.navigate(Screen.Welcome.route) {
                                        popUpTo(Screen.Welcome.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Definição das rotas da aplicação
sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object EditProfile : Screen("editProfile")
    object ListUsers : Screen("listUsers")
    object CashFlow : Screen("cashFlow")
    object Beneficiario : Screen("beneficiario")
    object Visita : Screen("visita")
    object Consulta : Screen("consulta")
    object AllVisitas : Screen("allVisitas")
    object AllNacionalidades : Screen("allNacionalidades")
}