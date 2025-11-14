package com.example.lojasocial.ui.consulta

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlin.random.Random


fun nacionalidadesPieChart(data: Map<String, Int>, colors: List<Color>): Bitmap {
    val total = data.values.sum()

    val angles = data.mapValues { (it.value / total.toFloat()) * 360f }

    val bitmap = Bitmap.createBitmap(250, 250, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val rect = RectF(0f, 0f, 250f, 250f) // Define o espaço onde o gráfico será desenhado

    var startAngle = 0f
    angles.entries.forEachIndexed { index, (_, angle) ->
        val paint = Paint().apply {
            color = colors[index].toArgb()  // Define a cor para o segmento
            isAntiAlias = true  // Ativa a suavização
        }
        canvas.drawArc(rect, startAngle, angle, true, paint) // Desenha cada segmento do gráfico
        startAngle += angle
    }

    return bitmap
}

// Função não composable para gerar a paleta de cores para o gráfico
fun generateColorPalette(size: Int): List<Color> {
    return List(size) {
        Color(
            red = Random.nextFloat(),
            green = Random.nextFloat(),
            blue = Random.nextFloat(),
            alpha = 1f
        )
    }
}

@Composable
fun DisplayNacionalidadesChart(data: Map<String, Int>, colors: List<Color>) {
    val total = data.values.sum()

    // Gerar e lembrar as cores apenas quando os dados mudarem
    val colors by remember(data) { mutableStateOf(generateColorPalette(data.size)) }

    // Gera o gráfico como um Bitmap
    val pieChartBitmap = nacionalidadesPieChart(data = data, colors = colors)

    // Exibe o gráfico como uma imagem
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Exibe o gráfico de pizza
        Image(
            bitmap = pieChartBitmap.asImageBitmap(),
            contentDescription = "Gráfico de Países ",
            modifier = Modifier.size(250.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Exibe a legenda com as porcentagens
        Column {
            data.entries.toList().forEachIndexed { index, entry ->
                val (nacionalidade, count) = entry
                val color = colors[index]
                val percentage = (count / total.toFloat()) * 100
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp) // Reduz o tamanho da cor na legenda
                            .background(color = color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$nacionalidade: $count (${String.format("%.1f", percentage)}%)",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
