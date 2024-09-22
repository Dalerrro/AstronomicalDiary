package com.example.startapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.startapp.ui.theme.StartAppTheme
import com.example.startapp.openGL.renderGL.MyGLSurfaceView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StartAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    OpenGLContent(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun OpenGLContent(modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            MyGLSurfaceView(context)
        },
        modifier = modifier.fillMaxSize() // Применяем отступы и заполняем доступное пространство
    )
}