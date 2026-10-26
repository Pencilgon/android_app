package com.example.lma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import com.example.lma.navigation.AppNavigation
import com.example.lma.ui.theme.LostMediaArchiveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LostMediaArchiveTheme {
                Surface(
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}