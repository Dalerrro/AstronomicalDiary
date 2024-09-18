package com.example.startapp

data class News (
    val id: Int,
    var content: String,
    var likes: Int = 0
)

val newsList = listOf(
    News(1, "Ancient artifact controls town's rainfall."),
    News(2, "Laughter extends life by decade."),
    News(3, "Blindfolded sushi eating becomes popular."),
    News(4, "Kangaroos teach yoga to humans."),
    News(5, "Midnight rainbow spotted in Alaska."),
    News(6, "Restaurant serves unsold food creatively."),
    News(7, "Penguins become popular Brazilian pets."),
    News(8, "Potato successfully grown in space."),
    News(9, "Strange hat museum opens in London."),
    News(10, "World's longest cigar festival held.")
)