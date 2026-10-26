package com.example.lma.navigation

sealed class Screen(val route: String) {

    data object Archive : Screen("archive")

    data object SubmitCase : Screen("submit_case")

    data object Watchlist : Screen("watchlist")

    data object CaseDetails : Screen("case_details/{caseId}") {
        fun createRoute(caseId: Int): String {
            return "case_details/$caseId"
        }
    }
}