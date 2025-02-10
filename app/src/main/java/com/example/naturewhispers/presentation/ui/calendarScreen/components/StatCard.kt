package com.example.naturewhispers.presentation.ui.calendarScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.naturewhispers.data.local.entities.Stat
import com.example.naturewhispers.data.utils.formatDuration
import com.example.naturewhispers.data.utils.formatDurationFromMillis
import com.example.naturewhispers.data.utils.formatTime
import com.example.naturewhispers.presentation.ui.theme.NatureWhispersTheme

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    stat: Stat,
) {

    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(10.dp),
    ) {

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(10.dp)

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stat.presetTitle)
                Text(text = formatDurationFromMillis(stat.duration), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Text(text = " at ${formatTime(stat.date)}", color = Color.DarkGray)

        }
    }
}

@Composable
@Preview
fun StatCardPreview() {
    NatureWhispersTheme {
        StatCard(stat = Stat())
    }
}