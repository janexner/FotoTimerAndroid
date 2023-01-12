package com.exner.tools.fototimerresearch2.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HeaderText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth(),
        style = MaterialTheme.typography.headlineMedium
    )
}

@Composable
fun BodyText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun ProcessStartButton(modifier: Modifier = Modifier) {
    FilledTonalButton(
        onClick = { /*TODO*/ }, modifier = Modifier
            .fillMaxSize(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(text = "Start", style = MaterialTheme.typography.headlineMedium)
    }
}

