package com.example.naturewhispers.presentation.ui.calendarScreen

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturewhispers.data.di.TAG
import com.example.naturewhispers.data.entities.Stat
import com.example.naturewhispers.data.local.db.PresetDao
import com.example.naturewhispers.data.local.db.StatDao
import com.example.naturewhispers.data.utils.ImmutableList
import com.example.naturewhispers.data.utils.isSameDate
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.Store
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.boguszpawlowski.composecalendar.kotlinxDateTime.now
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@HiltViewModel
class CalendarViewModel @Inject constructor(
    statDao: StatDao,
): ViewModel() {

    private val _state = mutableStateOf(CalendarState())
    val uiState: State<CalendarState> = _state

    init {
        viewModelScope.launch {
            statDao.getStats().collectLatest {
                    _state.value = _state.value.copy(
                            stats = ImmutableList(it)
                        )
                }
        }
    }

    fun sendEvent(event: CalendarEvents) {
        when (event) {
            is CalendarEvents.OnSelectDate -> updateSelectedDate(event.date)
        }

    }

    private fun updateSelectedDate(date: LocalDate) {
        _state.value = _state.value.copy(selectedDate = date)
    }
}

data class CalendarState(
    val stats: ImmutableList<Stat> = ImmutableList(),
    val selectedDate: LocalDate = LocalDate.now(),
)

sealed interface CalendarEvents {
    data class OnSelectDate(val date: LocalDate) : CalendarEvents
}