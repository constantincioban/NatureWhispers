package com.example.naturewhispers.presentation.ui.profileScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.naturewhispers.presentation.ui.profileScreen.ProfileEvents

@Composable
fun DataLoadingDialog(
    modifier: Modifier = Modifier,
    text: String,
) {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = modifier
                    .clip(RoundedCornerShape(18.dp))
                    .fillMaxWidth(0.6f)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(vertical = 20.dp)
            ) {

                Text(text = text, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                CircularProgressIndicator(

                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
            }
    }
}