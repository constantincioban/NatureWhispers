package com.example.naturewhispers.presentation.ui.calendarScreen

import android.annotation.SuppressLint
import android.os.Debug
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.naturewhispers.data.di.TAG
import com.example.naturewhispers.data.entities.Stat
import com.example.naturewhispers.data.utils.ImmutableList
import com.example.naturewhispers.data.utils.isSameDate
import com.example.naturewhispers.presentation.ui.calendarScreen.components.StatCard
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.kotlinxDateTime.now
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate
import java.util.concurrent.TimeUnit

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
) {

    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        ) {
        Content(viewModel.uiState.value.stats)
    }
}

@Composable
fun Content(
    stats: ImmutableList<Stat>,
) {

    val calendarState = rememberSelectableCalendarState()
    var statsFiltered by remember {
        mutableStateOf(ImmutableList<Stat>())
    }
    var selection by remember {
        mutableStateOf(LocalDate.now())
    }


    LaunchedEffect(key1 = selection, key2 = stats) {
        statsFiltered = ImmutableList(stats.filter {
            isSameDate(it.date, selection.toString())
        })
    }
    /*LaunchedEffect(Unit) {
        delay(1000)
        Debug.stopMethodTracing()
    }*/

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        SelectableCalendar(
            calendarState = calendarState,
            showAdjacentMonths = false,
            dayContent = { selected ->

                val selectedDate = selected.date.toKotlinLocalDate().toString()

                val statsForSelectedDay = stats.filter { isSameDate(it.date, selectedDate) }
                val anyStatsInTheSelectedDay = statsForSelectedDay.isNotEmpty()

                val shapeRoundedValue = if (anyStatsInTheSelectedDay) 50.dp else null

                val totalDuration = statsForSelectedDay.sumOf { TimeUnit.MILLISECONDS.toSeconds(it.duration) }.toInt() / 60
                val currentGoal = statsForSelectedDay.lastOrNull()?.currentGoal ?: 1
                Log.i(TAG, "Content: ${selected.date.dayOfMonth} currentGoal = $currentGoal , totalDuration = $totalDuration")
                val border = if (totalDuration >= currentGoal)
                    BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
                else
                    null

                val color = when {
                    selection == selected.date -> MaterialTheme.colorScheme.primary
                    anyStatsInTheSelectedDay -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.onSurface
                }
                Card(
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(shapeRoundedValue ?: 5.dp),
                    border = border,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                        .height(42.dp)
                        .clickable {
                            selection = selected.date
                            Log.i(TAG, "[Calendar] selection = : $selection")
                        },
                    ) {
                    Text(
                        text = selected.date.dayOfMonth.toString(),
                        fontSize = 18.sp,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 7.dp),
                        textAlign = TextAlign.Center,
                        color = color
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))
        if (statsFiltered.isEmpty()) {
            Spacer(modifier = Modifier.fillMaxHeight(0.3f))
            Text(text = "No stats for current date", fontSize = 20.sp)

        } else
            LazyColumn(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(15.dp),
                contentPadding = PaddingValues(bottom = 74.dp),
            ) {
                items(items = statsFiltered, key = { stat -> stat.id }) {
                    StatCard(stat = it)
                }
            }
        Spacer(modifier = Modifier.height(20.dp))

    }
}