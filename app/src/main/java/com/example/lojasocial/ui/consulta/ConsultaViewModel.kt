package com.example.lojasocial.ui.consulta


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lojasocial.repository.BeneficiarioRepository
import com.example.lojasocial.repository.VisitaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log
import androidx.compose.ui.graphics.toArgb
import com.example.lojasocial.R
import java.io.File
import java.io.FileOutputStream


data class ConsultaState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val colors: List<Color> = emptyList(),
    val nacionalidadesTotais: Map<String, Int> = emptyMap(),
    val visitasPorAno: Map<Int, Int> = emptyMap(),
    val nacionalidadeCores: Map<String, Color> = emptyMap()
)

class ConsultaViewModel : ViewModel() {

    var state = mutableStateOf(ConsultaState())
        private set

    // Função para armazenar as cores
    fun setColors(colors: List<Color>) {
        state.value = state.value.copy(colors = colors)
    }


    fun loadNacionalidadesTotais() {
        state.value = state.value.copy(isLoading = true, errorMessage = null)

        BeneficiarioRepository.getAllBeneficiarios(
            onSuccess = { beneficiarios ->
                val nacionalidadesTotais = beneficiarios
                    .groupingBy { it.nacionalidade }
                    .eachCount()

                state.value = state.value.copy(
                    nacionalidadesTotais = nacionalidadesTotais,
                    isLoading = false
                )
            },
            onFailure = { errorMessage ->
                state.value = state.value.copy(
                    errorMessage = errorMessage,
                    isLoading = false
                )
            }
        )
    }

    fun loadVisitasPorAno() {
        state.value = state.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch(Dispatchers.IO) {
            VisitaRepository.getAllVisitas(
                onSuccess = { visitas ->
                    try {
                        val visitasPorAno = visitas.groupBy { visita ->
                            val calendar = java.util.Calendar.getInstance()
                            calendar.timeInMillis = visita.data
                            calendar.get(java.util.Calendar.YEAR) // Garante que retorna o ano como Int
                        }.mapValues { it.value.size }

                        state.value = state.value.copy(
                            visitasPorAno = visitasPorAno,
                            isLoading = false
                        )
                    } catch (e: Exception) {
                        state.value = state.value.copy(
                            errorMessage = "Erro ao processar visitas: ${e.message}",
                            isLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    state.value = state.value.copy(
                        isLoading = false,
                        errorMessage = error
                    )
                }
            )
        }
    }


    fun exportToPdf(
        data: Map<String, Int>,
        visitasPorAno: Map<Int, Int>,
        nacionalidadesTotais: Map<String, Int>,
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
        showLoading: () -> Unit,
        hideLoading: () -> Unit
    ) {
        showLoading()

        val pdfDocument = PdfDocument()

        try {
            val pageInfo = PdfDocument.PageInfo.Builder(600, 800, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            // Paint para o título com fonte maior
            val titlePaint = Paint().apply {
                textSize = 20f // Tamanho maior para o título
                color = android.graphics.Color.BLACK
                isAntiAlias = true
            }

            // Paint para o restante do conteúdo
            val contentPaint = Paint().apply {
                textSize = 16f
                color = android.graphics.Color.BLACK
            }

            // Paint para a legenda (tamanho menor)
            val legendPaint = Paint().apply {
                textSize = 12f  // Fonte menor para a legenda
                color = android.graphics.Color.BLACK
            }

            // Gerar as cores para o gráfico e legenda
            val colors = generateColorPalette(data.size)

            // Carregar o logo (imagem)
            val logoBitmap: Bitmap = BitmapFactory.decodeResource(
                context.resources,
                R.drawable.logo // Substitua com o ID do seu logo
            )


            val logoWidth = 100f
            val logoHeight = 100f
            val scaledLogoBitmap = Bitmap.createScaledBitmap(logoBitmap, logoWidth.toInt(), logoHeight.toInt(), false)

            // Desenhar o logo redimensionado
            val logoXPosition = 30f
            val logoYPosition = 0f
            canvas.drawBitmap(scaledLogoBitmap, logoXPosition, logoYPosition, null) // Desenha o logo

            // Ajusta a posição do título para ficar ao lado do logo
            val titleXPosition = logoXPosition + logoWidth + 10f // Posição do título ao lado do logo (10f de espaço)
            val titleYPosition = logoYPosition + logoHeight / 2f // Alinhar verticalmente com o centro do logo


            // Desenha o título abaixo do logo
            canvas.drawText("Relatório de Países e Visitas", titleXPosition, titleYPosition, titlePaint)

            // Adicionar uma barra abaixo do título
            val barPaint = Paint().apply {
                color = android.graphics.Color.GRAY
                strokeWidth = 5f // Espessura da barra
            }
            canvas.drawLine(50f, titleYPosition + 30f, 550f, titleYPosition + 30f, barPaint)


            var yPosition = titleYPosition + 50f


            canvas.drawText("Paises:", 50f, yPosition, contentPaint)
            yPosition += 30f
            nacionalidadesTotais.forEach { (nacionalidade, total) ->
                canvas.drawText("$nacionalidade: $total visitas", 50f, yPosition, contentPaint)
                yPosition += 20f // Espaço entre os itens
            }

            // Gera o gráfico como Bitmap
            val pieChartBitmap = nacionalidadesPieChart(data = data, colors = colors) // Gera o gráfico com as cores passadas

            val pieChartWidth = 200f
            val pieChartHeight = 200f
            val scaledPieChartBitmap = Bitmap.createScaledBitmap(pieChartBitmap, pieChartWidth.toInt(), pieChartHeight.toInt(), false)

            canvas.drawBitmap(scaledPieChartBitmap, 50f, yPosition + 20f, null)
            yPosition += 240f

            // Adiciona a legenda
            canvas.drawText("Legenda:", 50f, yPosition, contentPaint)
            yPosition += 30f

            data.entries.toList().forEachIndexed { index, entry ->
                val (nacionalidade, count) = entry
                val percentage = (count / data.values.sum().toFloat()) * 100

                val colorPaint = Paint().apply {
                    color = colors[index].toArgb()  // Agora usa as cores passadas
                }
                canvas.drawRect(50f, yPosition, 70f, yPosition + 10f, colorPaint)

                legendPaint.color = android.graphics.Color.BLACK  // Usando legendPaint aqui para a legenda
                canvas.drawText(
                    "$nacionalidade: $count (${String.format("%.1f", percentage)}%)",
                    80f, yPosition + 5f, legendPaint  // Usando legendPaint para desenhar os itens da legenda
                )

                yPosition += 20f  // Espaço entre os itens da legenda
            }

            // Adiciona um espaço extra entre a legenda e "Visitas por Ano"
            yPosition += 30f  // Adiciona mais espaço entre a legenda e "Visitas por Ano"

            // Adiciona as Visitas por Ano
            canvas.drawText("Visitas por Ano:", 50f, yPosition, contentPaint)
            yPosition += 30f
            visitasPorAno.forEach { (ano, totalVisitas) ->
                canvas.drawText("$ano: $totalVisitas visitas", 50f, yPosition, contentPaint)
                yPosition += 20f
            }

            // **Chama finishPage() APÓS desenhar tudo**
            pdfDocument.finishPage(page)

            // Grava o PDF no diretório desejado
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            if (!downloadsDir.exists() && !downloadsDir.mkdirs()) {
                onFailure("Erro ao criar diretório de documentos.")
                hideLoading()
                return
            }

            val timestamp = System.currentTimeMillis()
            val outputFile = File(downloadsDir, "relatorio_visitas_$timestamp.pdf")
            pdfDocument.writeTo(FileOutputStream(outputFile))

            onSuccess()  // Chama onSuccess após tudo ser concluído
        } catch (e: Exception) {
            onFailure("Erro ao exportar PDF: ${e.message}")
            Log.e("PDFExportError", "Erro ao exportar PDF: ${e.message}")
        } finally {
            hideLoading()  // Esconde a janela de carregamento após a operação terminar
            pdfDocument.close()  // Certifique-se de chamar close() no final
        }
    }


}






