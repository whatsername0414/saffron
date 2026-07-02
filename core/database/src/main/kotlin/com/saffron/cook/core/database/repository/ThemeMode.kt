package com.saffron.cook.core.database.repository

enum class ThemeMode(
    val key: String,
) {
    Light("light"),
    Dark("dark"),
    System("system"),
    ;

    companion object {
        fun fromKey(key: String?): ThemeMode = entries.firstOrNull { it.key == key } ?: System
    }
}
