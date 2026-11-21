package com.example.lma.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lma.viewmodel.ArchiveViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GlobalSnackbar(viewModel: ArchiveViewModel) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val errorMessage = uiState.errorMessage
    val successMessage = uiState.successMessage

    LaunchedEffect(errorMessage, successMessage) {
        val message = errorMessage ?: successMessage
        ?: return@LaunchedEffect

        val snackbarJob = launch {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Indefinite
            )
        }

        delay(2500)

        snackbarHostState.currentSnackbarData?.dismiss()
        snackbarJob.cancel()

        if (errorMessage != null) {
            viewModel.clearError()
        } else {
            viewModel.clearSuccessMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        SnackbarHost(
            hostState = snackbarHostState
        ) { snackbarData ->

            val isError = errorMessage != null

            Snackbar(
                snackbarData = snackbarData,
                containerColor = if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                },
                contentColor = if (isError) {
                    MaterialTheme.colorScheme.onError
                } else {
                    MaterialTheme.colorScheme.onPrimary
                }
            )
        }
    }
}