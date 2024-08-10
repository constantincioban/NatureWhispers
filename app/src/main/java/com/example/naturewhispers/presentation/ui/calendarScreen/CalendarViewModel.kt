package com.example.naturewhispers.presentation.ui.calendarScreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturewhispers.data.entities.Stat
import com.example.naturewhispers.data.local.db.PresetDao
import com.example.naturewhispers.data.local.db.StatDao
import com.example.naturewhispers.data.utils.ImmutableList
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.Store
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    statDao: StatDao,
): ViewModel() {

    private val _stats = mutableStateOf(ImmutableList<Stat>())
    val stats: State<ImmutableList<Stat>> = _stats

    init {
        viewModelScope.launch {
            statDao.getStats().collectLatest {
                _stats.value = ImmutableList(it)
            }
        }
    }
}

