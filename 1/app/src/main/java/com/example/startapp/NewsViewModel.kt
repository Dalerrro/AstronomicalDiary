package com.example.startapp

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {

    val currentNews = mutableStateListOf<News>()

    init {

        currentNews.addAll(newsList.shuffled().take(4))
        startNewsUpdate()
    }

    private fun startNewsUpdate() {
        viewModelScope.launch {
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
    }

    fun increaseLikes(newsId: Int) {
        val newsItem = currentNews.find { it.id == newsId }
        newsItem?.let {
            it.likes++
        }
    }
}