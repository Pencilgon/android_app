package com.example.lma.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.lma.model.MediaCase

@Composable
fun MediaCaseCard(
    mediaCase: MediaCase,
    onClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mediaCase.category.displayName.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "#CASE-${mediaCase.id.toString().padStart(3, '0')}",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Text(
                text = mediaCase.title,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = mediaCase.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "LAST KNOWN",
                        style = MaterialTheme.typography.labelSmall
                    )

                    Text(
                        text = mediaCase.lastKnownYear?.toString() ?: "Unknown",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                StatusChip(status = mediaCase.recoveryStatus)
            }

            TextButton(
                onClick = onBookmarkClick
            ) {
                Text(
                    text = if (mediaCase.isBookmarked) {
                        "★ SAVED TO WATCHLIST"
                    } else {
                        "☆ SAVE TO WATCHLIST"
                    }
                )
            }
        }
    }
}