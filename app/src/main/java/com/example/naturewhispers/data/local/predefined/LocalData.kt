package com.example.naturewhispers.data.local.predefined

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.Forest
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Water
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.naturewhispers.data.local.models.Audio


object LocalData {

    val meditationSounds: Map<Audio, ImageVector> = mapOf(
        Pair(Audio(
            artist = "Nature",
            title = "Bonfire",
            subtitle = "",
            uri ="https://www.dropbox.com/scl/fi/e0lvo15n3uz0o9dsk2tmg/Bonfire.mp3?rlkey=449cq46hmxqmoqh7kckvjgudd&st=1v8v3v9w&dl=1"
        ), Icons.Rounded.LocalFireDepartment),
        Pair(Audio(
            artist = "Nature",
            title = "Rain",
            subtitle = "",
            uri ="https://www.dropbox.com/scl/fi/30slnc7uwa7gc5dvkfc5a/Rain.mp3?rlkey=hu8g8vm5in2ez8y4hiy25bq9c&st=uei1m5xg&dl=1"
        ), Icons.Rounded.Water),
        Pair(Audio(
            artist = "Nature",
            title = "Forest",
            subtitle = "",
            uri ="https://www.dropbox.com/scl/fi/n84sncdo8jcfrf994wlsb/Forest.mp3?rlkey=nf3d9y80rouux2p41jzv7cvue&st=h9c98lrn&dl=1"
        ), Icons.Rounded.Forest)
    )
}

