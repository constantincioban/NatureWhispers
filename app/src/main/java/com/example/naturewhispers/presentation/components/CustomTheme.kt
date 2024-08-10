package com.example.naturewhispers.presentation.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.example.naturewhispers.presentation.ui.theme.NatureWhispersTheme

@Composable
fun NWCustomTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  NatureWhispersTheme(
    darkTheme = darkTheme,
    content = content
  )
}