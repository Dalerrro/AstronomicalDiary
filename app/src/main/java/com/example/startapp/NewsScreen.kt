package com.example.startapp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewsScreen() {
    val currentNews = remember { mutableStateListOf<News>() }

    LaunchedEffect(Unit) {
        currentNews.addAll(newsList.shuffled().take(4))
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)

            val indexToReplace = (0 until currentNews.size).random()


            val availableNews = newsList.filter { it !in currentNews }
            if (availableNews.isNotEmpty()) {
                val newNews = availableNews.random()

                currentNews[indexToReplace] = newNews
            }
        }
    }

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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = news.content,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        )


        var likes by remember { mutableStateOf(news.likes) }
        Button(
            onClick = {
                likes++
                news.likes = likes
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Likes: $likes")
        }
    }
}

