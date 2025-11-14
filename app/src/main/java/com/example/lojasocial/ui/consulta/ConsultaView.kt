package com.example.lojasocial.ui.consulta


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lojasocial.R
import java.util.*


@Composable
fun ConsultaView(
    onNavigateToAllVisitas: () -> Unit,
    onNavigateToAllNacionalidades: () -> Unit,
    viewModel: ConsultaViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.value
    val anoAtual = Calendar.getInstance().get(Calendar.YEAR)
    val context = LocalContext.current
    val showLoading = remember { mutableStateOf(false) }


    // Gerar as cores uma vez ao entrar na tela
    LaunchedEffect(Unit) {
        val colors = generateColorPalette(state.nacionalidadesTotais.size)
        viewModel.setColors(colors)
    }


    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Visitas:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (state.isLoading) {
                item {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else if (state.errorMessage != null) {
                item {
                    Text(
                        text = "Erro: ${state.errorMessage}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total de Visitas em $anoAtual:",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "${state.visitasPorAno[anoAtual] ?: 0}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = onNavigateToAllVisitas) {
                        Text(text = "Ver Todas as Visitas")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Países representados na LojaSocial:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    DisplayNacionalidadesChart(
                        data = state.nacionalidadesTotais,
                        colors = state.colors
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = onNavigateToAllNacionalidades) {
                        Text(text = "Ver Todas as Nacionalidades")
                    }
                }
            }
        }

            if (showLoading.value) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            FloatingActionButton(
                onClick = {
                    viewModel.exportToPdf(
                        data = state.nacionalidadesTotais,
                        visitasPorAno = state.visitasPorAno,
                        nacionalidadesTotais = state.nacionalidadesTotais,
                        context = context,
                        onSuccess = {
                            Toast.makeText(
                                context,
                                "PDF exportado com sucesso!",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onFailure = { error ->
                            Toast.makeText(
                                context,
                                "Erro ao exportar PDF: $error",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.e("PDFExportError", "Erro ao exportar PDF: $error")
                        },
                        showLoading = { showLoading.value = true },
                        hideLoading = { showLoading.value = false }
                    )
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.download),
                    contentDescription = "Exportar PDF",
                    tint = Color.White
                )
            }

            LaunchedEffect(Unit) {
                viewModel.loadVisitasPorAno()
                viewModel.loadNacionalidadesTotais()
            }
        }
    }