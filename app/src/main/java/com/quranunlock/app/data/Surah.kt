package com.quranunlock.app.data

data class Surah(
    val number: Int,
    val name: String,
    val arabicName: String,
    val urduName: String,
    val ayahCount: Int,
    val startAyahGlobal: Int
)

data class QuranVerse(
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahName: String,
    val arabicName: String,
    val urduName: String,
    val globalAyahIndex: Int,
    val arabicAudioUrl: String,
    val urduAudioUrl: String
)
