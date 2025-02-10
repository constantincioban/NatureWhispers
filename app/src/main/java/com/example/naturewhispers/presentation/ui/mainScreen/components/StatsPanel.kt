package com.example.naturewhispers.presentation.ui.mainScreen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.naturewhispers.data.local.entities.Stat
import com.example.naturewhispers.data.utils.ImmutableList
import com.example.naturewhispers.data.utils.countConsecutiveDates
import com.example.naturewhispers.data.utils.isSameDate
import com.example.naturewhispers.presentation.ui.theme.NatureWhispersTheme
import java.time.Instant
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun StatsPanel(
    modifier: Modifier = Modifier,
    dailyGoal: Float = 0f,
    todaysTime: Int,
    streak: Int,

    ) {
    Card(
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(  )
    ) {

        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "Today's time", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Box(
                        contentAlignment = Alignment.Center, modifier = Modifier
                            .size(100.dp)
                            .padding(5.dp)
                    ) {
                        ProgressArc(
                            backgroundColor = MaterialTheme.colorScheme.background,
                            fillColor = MaterialTheme.colorScheme.primary,
                            percentage = if (todaysTime == 0) 0f else
                                if ((todaysTime / dailyGoal) > 1) 1f else (todaysTime / dailyGoal),
                            strokeWidth = 10.dp
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = todaysTime.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                            Text(text = "min", fontSize = 16.sp)

                        }
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                ) {
                    Text(text = "Streak", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(15.dp))
                    Image(
                        imageVector = Icons.Rounded.LocalFireDepartment,
                        contentDescription = "Fire",
                        contentScale = ContentScale.Crop,            // crop the image if it's not a square
                        modifier = Modifier
                            .padding(0.dp)
                            .size(60.dp)
                            .clip(CircleShape),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    )
                    Text(text = "$streak ${if (streak == 1) "day" else "days"}")

                }
            }
        }


    }

}

@Preview
@Composable
fun StatsPanelPreview() {
    NatureWhispersTheme {
        StatsPanel(
            todaysTime = 2,
            streak = 1
        )
    }
}