package com.example.naturewhispers.presentation.ui.mainScreen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.naturewhispers.data.entities.Preset
import com.example.naturewhispers.data.local.predefined.LocalData
import com.example.naturewhispers.data.utils.formatDuration
import com.example.naturewhispers.data.utils.formatDurationFromMillis

@Composable
fun SquareCard(
    modifier: Modifier = Modifier,
    item: Preset,
    onClick: (Int) -> Unit,

) {

    val imageVector = LocalData.meditationSounds
        .map { Pair(it.key.title, it.value) }.toMap()[item.sound] ?: Icons.Rounded.AutoAwesome

    Card(
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.clickable { onClick(item.id) }
    ) {
        Column(
            modifier = modifier.size(100.dp).padding(5.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = item.title, lineHeight = 20.sp)
            Icon(imageVector = imageVector, contentDescription = item.title)
            Text(text = formatDurationFromMillis(item.duration * 1000L))

        }
    }

}