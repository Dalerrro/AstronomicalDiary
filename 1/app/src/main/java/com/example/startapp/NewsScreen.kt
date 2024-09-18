package com.example.startapp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewsScreen(viewModel: NewsViewModel = viewModel()) {

    val currentNews = viewModel.currentNews

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(currentNews.size) { index ->
            NewsItem(news = currentNews[index])
        }
    }
}

@Composable
fun NewsItem(news: News) {
    var localLikes by remember(news.id) { mutableStateOf(news.likes) }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight * 0.45f)
            .padding(8.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = news.content,
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    localLikes++
                    news.likes = localLikes
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "Likes: $localLikes",

                )
            }
        }
    }
}
