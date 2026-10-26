package com.example.lma.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lma.ui.archive.ArchiveScreen
import com.example.lma.ui.details.CaseDetailsScreen
import com.example.lma.ui.submit.SubmitCaseScreen
import com.example.lma.ui.watchlist.WatchlistScreen
import com.example.lma.viewmodel.ArchiveViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val archiveViewModel: ArchiveViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Archive.route
    ) {
        composable(Screen.Archive.route) {
            ArchiveScreen(
                viewModel = archiveViewModel,
                onCaseClick = { caseId ->
                    navController.navigate(
                        Screen.CaseDetails.createRoute(caseId)
                    )
                },
                onSubmitClick = {
                    navController.navigate(Screen.SubmitCase.route)
                },
                onWatchlistClick = {
                    navController.navigate(Screen.Watchlist.route)
                }
            )
        }

        composable(Screen.SubmitCase.route) {
            SubmitCaseScreen(
                viewModel = archiveViewModel,
                onBack = {
                    navController.popBackStack()
                },
                onSubmitted = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Watchlist.route) {
            WatchlistScreen(
                viewModel = archiveViewModel,
                onCaseClick = { caseId ->
                    navController.navigate(
                        Screen.CaseDetails.createRoute(caseId)
                    )
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.CaseDetails.route,
            arguments = listOf(
                navArgument("caseId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val caseId = requireNotNull(
                backStackEntry.arguments?.getInt("caseId")
            )

            CaseDetailsScreen(
                caseId = caseId,
                viewModel = archiveViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}