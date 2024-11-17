package com.example.startapp.openGL.solarSystem

import android.content.Context
import android.opengl.GLSurfaceView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.sp
import com.example.startapp.openGL.renderGL.InfoScreenRenderer

@Composable
fun InfoScreen(selectedPlanetIndex: Int) {
    val context = LocalContext.current
    val infoText = getObjectInfo(selectedPlanetIndex)

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            AndroidView(
                factory = { ctx ->
                    ObjectGLSurfaceView(ctx, selectedPlanetIndex)
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(color = Color.Transparent)
        ) {
            Text(
                text = infoText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = Color.LightGray,
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }


fun getObjectInfo(index: Int): String {
    return when (index) {
        0 -> "Меркурий — самая маленькая планета в Солнечной системе, находящаяся ближе всех к Солнцу. Она быстро вращается вокруг своей оси и завершает полный оборот вокруг Солнца всего за 88 земных дней."
        1 -> "Венера — вторая по удалённости от Солнца планета, известная своим ярким светом и облаками серной кислоты. Несмотря на схожесть с Землёй, её атмосфера делает её самой горячей планетой Солнечной системы."
        2 -> "Земля — единственная известная планета, на которой существует жизнь. С уникальной атмосферой и системой водоемов она поддерживает разнообразие экосистем и климатов."
        3 -> "Марс — известен как 'Красная планета' из-за своего ржаво-красного цвета, вызванного оксидом железа на поверхности. Исследования показывают, что на Марсе могут быть следы древней воды."
        4 -> "Юпитер — самый массивный объект в Солнечной системе, обладающий мощным магнитным полем и более чем 70 спутниками, включая самый большой спутник Ганимед."
        5 -> "Сатурн — известен своими величественными кольцами, состоящими из льда и камней. Это газовый гигант, обладающий уникальной системой колец и многочисленными спутниками."
        6 -> "Уран — уникален своей осевой наклоном на бок, что делает его вращение отличным от других планет. Это также ледяной гигант с атмосферой, богатой метаном."
        7 -> "Нептун — последний известный объект Солнечной системы, обладает ярко-синим цветом из-за метана в атмосфере и имеет самый сильный ветер среди всех планет."
        8 -> "Луна — наш ближайший спутник, который оказывает влияние на приливы на Земле. Она состоит из горных и вулканических пород и является объектом интереса для будущих исследований."
        9 -> "Солнце — звезда, обеспечивающая свет и тепло для всей Солнечной системы. Оно образовалось примерно 4.6 миллиарда лет назад и является основным источником энергии для жизни на Земле."
        else -> "Неизвестный объект"
    }
}

class ObjectGLSurfaceView(context: Context, private val selectedPlanetIndex: Int) : GLSurfaceView(context) {
    init {
        setEGLContextClientVersion(2)
        setRenderer(InfoScreenRenderer(context, selectedPlanetIndex))
    }
}