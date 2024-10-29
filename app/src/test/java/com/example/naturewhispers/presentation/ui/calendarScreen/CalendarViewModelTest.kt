package com.example.naturewhispers.presentation.ui.calendarScreen

import app.cash.turbine.test
import com.example.naturewhispers.MainDispatcherRule
import com.example.naturewhispers.data.entities.Stat
import com.example.naturewhispers.data.local.db.StatDao
import com.example.naturewhispers.data.local.db.StatDaoFake
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.LocalDate

@RunWith(JUnit4::class)
class CalendarViewModelTest{

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: CalendarViewModel
    private lateinit var statDao: StatDao

    @Before
    fun setUp() {
        statDao = StatDaoFake()
        viewModel = CalendarViewModel(statDao)
    }

    @Test
    fun `updateSelectedDate, updates selectedDate in state`() {
        val date = LocalDate.now()
        viewModel.sendEvent(CalendarEvents.OnSelectDate(date))
        assertThat(viewModel.uiState.value.selectedDate).isEqualTo(date)
    }

    @Test
    fun `stats contain all stats from statDao`() = runTest {
        (statDao as StatDaoFake).insertThreeStreakStats()
        val stats = mutableListOf<Stat>()
        statDao.getStats().test {
            stats.addAll(awaitItem())
            cancelAndConsumeRemainingEvents()
        }

        viewModel = CalendarViewModel(statDao)
        assertThat(viewModel.uiState.value.stats).isEqualTo(stats)

    }


}