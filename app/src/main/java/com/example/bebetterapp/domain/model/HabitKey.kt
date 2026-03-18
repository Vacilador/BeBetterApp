package com.example.bebetterapp.domain.model

enum class HabitKey(
    val trackStreak: Boolean
) {
    ALCOHOL(false),
    SPORT(true),
    SLEEP_OK(true),
    SELF_DEV(true),
    FAMILY(false),
    FRIENDS(false)
}