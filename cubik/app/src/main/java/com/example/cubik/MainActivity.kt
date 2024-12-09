package com.example.cubik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cubik.openGL.MyGLRenderer
import com.example.cubik.openGL.MyGLSurfaceView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                MainNavRouter()
            }
        }
    }

@Composable
fun MainNavRouter() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "opengl") {
        composable("opengl") {
            OpenGLScreen(navController)
        }
    }
}

@Composable
fun OpenGLScreen(navController: NavController) {
    val context = LocalContext.current
    val renderer = remember { MyGLRenderer(context) }

    AndroidView(
        factory = { ctx ->
            MyGLSurfaceView(ctx, renderer)
        }
        )
    }
