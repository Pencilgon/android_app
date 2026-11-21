package com.example.lma.ui.watchlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lma.ui.components.MediaCaseCard
import com.example.lma.viewmodel.ArchiveViewModel

@Composable
fun WatchlistScreen(
    viewModel: ArchiveViewModel,
    onCaseClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val bookmarkedCases = uiState.cases.filter {
        it.isBookmarked
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            TextButton(onClick = onBack) {
                Text("← Back to archive")
            }

            Text(
                text = "PERSONAL COLLECTION",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Watchlist",
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = "${bookmarkedCases.size} saved cases",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(
                    top = 4.dp,
                    bottom = 16.dp
                )
            )

            if (bookmarkedCases.isEmpty()) {
                Text(
                    text = "Your watchlist is empty.",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Browse the archive and save cases you want to follow.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = bookmarkedCases,
                        key = { mediaCase -> mediaCase.id }
                    ) { mediaCase ->
                        MediaCaseCard(
                            mediaCase = mediaCase,
                            onClick = {
                                onCaseClick(mediaCase.id)
                            },
                            onBookmarkClick = {
                                viewModel.toggleBookmark(mediaCase.id)
                            }
                        )
                    }
                }
            }
        }
    }
}