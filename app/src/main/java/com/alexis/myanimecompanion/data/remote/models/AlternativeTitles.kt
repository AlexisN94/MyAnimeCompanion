package com.alexis.myanimecompanion.data.remote.models

data class AlternativeTitles(
    val en: String = "",
    val ja: String = "",
    val synonyms: List<String> = listOf()
)