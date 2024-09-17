package com.example.startapp

data class News (
    val id: Int,
    val content: String,
    var likes: Int = 0
)

private val newsList = listOf(
    News(1, "News 1"),
    News(2, "News 2"),
    News(3, "News 3"),
    News(4, "News 4"),
    News(5, "News 5"),
    News(6, "News 6"),
    News(7, "News 7"),
    News(8, "News 8"),
    News(9, "News 9"),
    News(10, "News 10")
)