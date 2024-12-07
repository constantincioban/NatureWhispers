package com.example.naturewhispers.presentation.ui.mainScreen.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.naturewhispers.data.entities.Preset
import com.example.naturewhispers.data.utils.ImmutableList

@Composable
fun PresetsSection(presets: ImmutableList<Preset>, toggleBottomSheet: (Int) -> Unit) {


    if (presets.isEmpty())
        Row(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(horizontal = 10.dp)
                .height(100.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "No presets")
        }
    else
        LazyRow(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,

            ) {
                items(presets, key = { it.id }) { preset ->
                    SquareCard(item = preset, onClick = { id ->
                        toggleBottomSheet(id)
                    })
                    Spacer(modifier = Modifier.width(10.dp))
                }
        }
}