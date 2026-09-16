package com.lilinth.questionsolver.ui.navigation

sealed class Screen(val route: String) {
    object Camera : Screen("camera")
    object Settings : Screen("settings")
    object Answer : Screen("answer")
}
