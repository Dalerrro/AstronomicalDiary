package com.example.startapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.startapp.advert.NewsScreen
import com.example.startapp.ui.theme.StartAppTheme
import com.example.startapp.openGL.renderGL.MyGLSurfaceView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.startapp.openGL.MyGLRenderer
import com.example.startapp.openGL.solarSystem.InfoScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StartAppTheme {
                MainNavRouter()
            }
        }
    }
}

    @Composable
    fun MainNavRouter() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "news") {
            composable("news") {
                NewsScreen(
                    onImageClick = { navController.navigate("opengl") }
                )
            }
            composable("opengl") {
                OpenGLScreen(navController)
            }
            composable("moon_info/{selectedPlanetIndex}") { backStackEntry ->
                val selectedPlanetIndex = backStackEntry.arguments?.getString("selectedPlanetIndex")?.toInt() ?: 0
                InfoScreen(selectedPlanetIndex = selectedPlanetIndex)
            }
        }
    }

    @Composable
    fun OpenGLScreen(navController: NavController) {
        var selectedPlanetIndex by remember { mutableIntStateOf(0) }
        val planetCount = 10

        val context = LocalContext.current
        val renderer = remember { MyGLRenderer(context) }

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { ctx ->
                    MyGLSurfaceView(ctx, renderer).apply {
                        setSelectedPlanet(selectedPlanetIndex)
                    }
                },
                update = { view ->
                    view.setSelectedPlanet(selectedPlanetIndex)
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(16.dp)
                    .wrapContentHeight(Alignment.Bottom)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        selectedPlanetIndex =
                            if (selectedPlanetIndex - 1 < 0) planetCount - 1 else selectedPlanetIndex - 1
                    }) {
                        Text("Влево")
                    }

                    Button(onClick = {
                        navController.navigate("moon_info/$selectedPlanetIndex")
                    }) {
                        Text("Информация")
                    }

                    Button(onClick = {
                        selectedPlanetIndex = (selectedPlanetIndex + 1) % planetCount
                    }) {
                        Text("Вправо")
                    }
                }
            }
        }
    }