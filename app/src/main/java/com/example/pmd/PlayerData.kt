package com.example.pmd

data class PlayerData (
    val fullName: String = "",
    val gender: String = "Мужской",
    val course: Int = 1,
    val difficulty: Int = 1,
    val birthDay: Int = 1,
    val birthMonth: Int = 1,
    val birthYear: Int = 2000,
    val zodiacSign: String = "Козерог"
)