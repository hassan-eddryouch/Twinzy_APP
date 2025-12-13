package com.example.twinzy_app.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Welcome : Screen("welcome")
    object SignIn : Screen("sign_in")
    object SignUp : Screen("sign_up")
    object PhoneAuth : Screen("phone_auth")
    object CompleteProfile : Screen("complete_profile")
    object Main : Screen("main")
    object Settings : Screen("settings")
    object EditProfile : Screen("edit_profile")
    object ChatDetail : Screen("chat_detail/{matchId}/{otherUserId}") {
        fun createRoute(matchId: String, otherUserId: String) = "chat_detail/$matchId/$otherUserId"
    }
}