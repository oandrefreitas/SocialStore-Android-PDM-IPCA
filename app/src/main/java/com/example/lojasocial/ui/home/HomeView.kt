package com.example.lojasocial.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lojasocial.R
import com.example.lojasocial.session.UserSession

@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    onVolunteerManagementClicked: () -> Unit = {},
    onCashFlowClicked: () -> Unit = {},
    onRegisterBeneficiaryClicked: () -> Unit = {},
    onEditProfileClicked: () -> Unit = {},
    onVisitaClicked: () -> Unit = {},
    onConsultaClicked: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logótipo da Loja Social",
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Primeira linha de botões
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CustomButton(
                text = "Gestão de Voluntário",
                icon = Icons.Default.Person,
                onClick = onVolunteerManagementClicked,
                isEnabled = (UserSession.getAccessLevel() ?: Int.MAX_VALUE) == 1
            )
            CustomButton(
                text = "Gestão de Caixa",
                icon = Icons.Default.ShoppingCart,
                onClick = onCashFlowClicked,
                isEnabled = (UserSession.getAccessLevel() ?: Int.MAX_VALUE) == 1
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Segunda linha de botões
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CustomButton(
                text = "Registar Beneficiário",
                icon = Icons.Default.Person,
                onClick = onRegisterBeneficiaryClicked,
                isEnabled = (UserSession.getAccessLevel() ?: Int.MAX_VALUE) <= 2
            )
            CustomButton(
                text = "Registar Visitas",
                icon = Icons.Default.Place,
                onClick = onVisitaClicked,
                isEnabled = (UserSession.getAccessLevel() ?: Int.MAX_VALUE) <= 2
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Terceira linha de botões
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CustomButton(
                text = "Editar Perfil",
                icon = Icons.Default.Edit,
                onClick = onEditProfileClicked,
                isEnabled = true
            )
            CustomButton(
                text = "Consultar Dados",
                icon = Icons.Default.Search,
                onClick = onConsultaClicked,
                isEnabled = (UserSession.getAccessLevel() ?: Int.MAX_VALUE) <= 3
            )
        }
    }
}

@Composable
fun CustomButton(
    text: String, // Texto do botão
    icon: ImageVector, // Ícone associado ao botão
    onClick: () -> Unit, // Callback para ação ao clicar
    isEnabled: Boolean = true // Indica se o botão está habilitado ou não
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface, // Cor do botão
            contentColor = if (isEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface // Cor do conteúdo
        ),
        shape = RoundedCornerShape(16.dp), // Define os cantos arredondados do botão
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp, // Elevação padrão
            pressedElevation = 12.dp, // Elevação quando pressionado
            disabledElevation = 0.dp // Elevação quando desativado
        ),
        modifier = Modifier.size(160.dp, 100.dp) // Define o tamanho fixo do botão
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround, // Espaçamento proporcional entre o ícone e o texto
            modifier = Modifier.fillMaxHeight() // Garante alinhamento vertical completo
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "$text Ícone", // Descrição para acessibilidade
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = text,
                fontSize = 14.sp,
                maxLines = 2, // Limita o texto a 2 linhas
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}