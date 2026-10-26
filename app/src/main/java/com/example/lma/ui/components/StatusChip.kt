package com.example.lma.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.lma.model.RecoveryStatus

@Composable
fun StatusChip(
    status: RecoveryStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status) {
        RecoveryStatus.LOST ->
            Color(0xFF3A2528) to Color(0xFFFF8A80)

        RecoveryStatus.PARTIALLY_FOUND ->
            Color(0xFF3A3420) to Color(0xFFFFD166)

        RecoveryStatus.FOUND ->
            Color(0xFF243627) to Color(0xFFC7F36B)
    }

    Text(
        text = status.displayName.uppercase(),
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
        color = textColor,
        style = MaterialTheme.typography.labelSmall
    )
}