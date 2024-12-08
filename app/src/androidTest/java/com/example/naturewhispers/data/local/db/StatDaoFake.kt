package com.example.naturewhispers.data.local.db

import com.example.naturewhispers.data.entities.Stat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StatDaoFake : StatDao {

    private val stats = mutableListOf<Stat>()


    fun insertZeroStreakStats() {
        stats.add(
            Stat(
                id = 3,
                duration = 250_000,
                date = System.currentTimeMillis() - 48 * 60 * 60 * 1000,
                presetTitle = "stat4",
                presetId = 2,
            )
        )
    }

    fun insertOneStreakStats() {
        stats.add(
            Stat(
                id = 3,
                duration = 250_000,
                date = System.currentTimeMillis(),
                presetTitle = "stat4",
                presetId = 2,
            )
        )
    }

    fun insertThreeStreakStats() {
        stats.addAll(
            listOf(
                Stat(
                    id = 3,
                    duration = 250_000,
                    date = System.currentTimeMillis(),
                    presetTitle = "stat4",
                    presetId = 2,
                ),
                Stat(
                    id = 3,
                    duration = 250_000,
                    date = System.currentTimeMillis() - 24 * 60 * 60 * 1000,
                    presetTitle = "stat4",
                    presetId = 2,
                ),
                Stat(
                    id = 3,
                    duration = 250_000,
                    date = System.currentTimeMillis() - 48 * 60 * 60 * 1000,
                    presetTitle = "stat4",
                    presetId = 2,
                ),
            )
        )
    }

    fun insertThreeHundredThousandTodaysTimeStats() {
        stats.addAll(
            listOf(

                Stat(
                    id = 3,
                    duration = 250_000,
                    date = System.currentTimeMillis() - 48 * 60 * 60 * 1000,
                    presetTitle = "stat4",
                    presetId = 2,
                ),

                Stat(
                    id = 1,
                    duration = 150_000,
                    date = System.currentTimeMillis(),
                    presetTitle = "stat1",
                    presetId = 2,
                ),

                Stat(
                    id = 2,
                    duration = 50_000,
                    date = System.currentTimeMillis(),
                    presetTitle = "stat2",
                    presetId = 2,
                ),

                Stat(
                    id = 3,
                    duration = 100_000,
                    date = System.currentTimeMillis(),
                    presetTitle = "stat3",
                    presetId = 2,
                ),

                )
        )
    }

    override suspend fun upsertStat(stat: Stat) {
        stats.add(stat)
    }

    override fun getStats(): Flow<List<Stat>> = flow { emit(stats) }

    override suspend fun getStatById(id: Int): Stat? = stats.find { it.id == id }


    override suspend fun deleteStat(stat: Stat) {
        stats.remove(stat)
    }

    override fun getStatsByPresetId(presetId: Int): Flow<List<Stat>> = flow {
        emit(stats.filter { it.presetId == presetId })
    }

    override suspend fun insertStats(stats: List<Stat>) {
        stats.forEach {
            upsertStat(it)
        }
    }

}