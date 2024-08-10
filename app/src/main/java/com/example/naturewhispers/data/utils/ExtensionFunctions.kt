package com.example.naturewhispers.data.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.naturewhispers.data.di.TAG
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@Immutable
data class ImmutableList<T>(
    val wrapped: List<T> = listOf()
) : List<T> by wrapped


@SuppressLint("Range")
fun getDisplayNameFromUri(uri: android.net.Uri, context: Context): String {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            return displayName.split(".")[0].replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.ENGLISH
                ) else it.toString()
            }
        }
    }
    return ""
}


fun getTodaysDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val currentDate = Date()
    return dateFormat.format(currentDate)
}

fun formatSecondsToMMss(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

fun isSameDate(dateMillis: Long, dateString: String): Boolean {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val dateFromMillis = Instant.ofEpochMilli(dateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
    val dateFromString = LocalDate.parse(dateString, formatter)

    return dateFromMillis.isEqual(dateFromString)
}

fun isSameMonth(dateInMillis: Long, month: String, zoneId: ZoneId = ZoneId.systemDefault()): Boolean {
    val date = Instant.ofEpochMilli(dateInMillis).atZone(zoneId).toLocalDate()
    val monthOfDate = date.monthValue.toString()

    return monthOfDate == month
}


fun countConsecutiveDates(datesInMillis: List<Long>, zoneId: ZoneId = ZoneId.systemDefault()): Int {
    val today = LocalDate.now(zoneId)
    var count = 0
    var previousDate = today

    for (dateInMillis in datesInMillis) {
        val date = Instant.ofEpochMilli(dateInMillis).atZone(zoneId).toLocalDate()
        if (date.isEqual(previousDate) || date.isEqual(previousDate.minusDays(1))) {
            count++
            previousDate = date
        } else {
            continue
        }
    }
    return count
}

fun isSameDate(duration1: Long, duration2: Long, zoneId: ZoneId = ZoneId.systemDefault()): Boolean {
    val date1 = Instant.ofEpochMilli(duration1).atZone(zoneId).toLocalDate()
    val date2 = Instant.ofEpochMilli(duration2).atZone(zoneId).toLocalDate()
    return date1.isEqual(date2)
}

fun oneDayInMillis(): Long {
    return 24 * 60 * 60 * 1000L
}

fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("EEE dd MMM, yyyy", Locale.ENGLISH)
    return dateFormat.format(Date())
}

fun formatTime(timeInMillis: Long): String {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return dateFormat.format(Date(timeInMillis))
}



fun formatDuration(durationInSeconds: Long): String {
    val hours = durationInSeconds / 3600
    val minutes = (durationInSeconds % 3600) / 60
    val seconds = durationInSeconds % 60

    return if (hours > 0 && seconds > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else if (hours > 0) {
        String.format("%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

fun formatDurationFromMillis(duration: Long): String {
    val hours = duration / 3600_000
    val minutes = duration / 60000
    val seconds = if (duration >= 60_000) (duration % 60_000) / 1000 else (duration / 1000)
    return if (hours > 0) {
        String.format("%02d:%02d", hours, minutes)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

inline fun <reified T> Gson.fromJson(json: String) =
    fromJson<T>(json, object : TypeToken<T>() {}.type)

@Composable
inline fun <reified T> Flow<T>.observeWithLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    noinline action: suspend (T) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        lifecycleOwner.lifecycleScope.launch {
            flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState).collect(action)
        }
    }
}

fun String.distinctChars(): String {
    var word = ""
    this.forEachIndexed { index, c ->
        if (index == 0) {
            word += c
            return@forEachIndexed
        }
        if (this[index] == this[index - 1])
            return@forEachIndexed
        word += c
    }
    return word
}

fun Date.formatToString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}
