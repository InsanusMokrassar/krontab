package com.insanusmokrassar.krontab.utils

import com.insanusmokrassar.krontab.buildSchedule
import com.soywiz.klock.DateTime
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile
import kotlin.math.max
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
@FlowPreview
class StringParseTest {
    @Test
    fun testThatFlowIsCorrectlyWorkEverySecondBuiltOnString() {
        val kronScheduler = buildSchedule("*/1 * * * *")

        val flow = kronScheduler.asFlow()

        runTest {
            val mustBeCollected = 10
            var collected = 0
            flow.takeWhile {
                collected < mustBeCollected
            }.collect {
                collected++
            }
            assertEquals(mustBeCollected, collected)
        }
    }

    @Test
    fun testThatFlowIsCorrectlyWorkEverySecondWithMuchOfEmittersBuiltOnString() {
        val kronScheduler = buildSchedule("*/1 * * * *")

        val flow = kronScheduler.asFlow()

        runTest {
            val testsCount = 10
            val failJob = it.createFailJob((testsCount * 2) * 1000L)
            val mustBeCollected = 10
            val answers = (0 until testsCount).map { _ ->
                it.async {
                    var collected = 0
                    flow.takeWhile {
                        collected < mustBeCollected
                    }.collect {
                        collected++
                    }
                    collected
                }
            }.awaitAll()

            failJob.cancel()

            answers.forEach {
                assertEquals(mustBeCollected, it)
            }
        }
    }
    @Test
    fun testThatFlowIsCorrectlyWorkEverySeveralSecondsRangeBuiltOnString() {
        val rangesEnds = listOf(0 to 5, 30 to 35)
        val kronScheduler = buildSchedule("${rangesEnds.joinToString(",") { "${it.first}-${it.second}" }} * * * *")

        val flow = kronScheduler.asFlow()

        runTest {
            val ranges = rangesEnds.map { it.first .. it.second }.flatten().toMutableList()
            val expectedCollects = rangesEnds.sumBy { it.second - it.first + 1 }
            var collected = 0

            flow.takeWhile { ranges.isNotEmpty() }.collect {
                ranges.remove(it.seconds)
                collected++
            }
            assertEquals(expectedCollects, collected)
        }
    }
}
