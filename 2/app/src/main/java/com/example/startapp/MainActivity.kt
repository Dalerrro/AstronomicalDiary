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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.startapp.advert.NewsScreen
import com.example.startapp.ui.theme.StartAppTheme
import com.example.startapp.openGL.renderGL.MyGLSurfaceView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StartAppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "news") {
                    composable("news") {
                        NewsScreen(
                            onImageClick = { navController.navigate("opengl") }
                        )
                    }
                    composable("opengl") {
                        OpenGLContent()
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
            modifier = modifier.fillMaxSize()
        )
    }
}