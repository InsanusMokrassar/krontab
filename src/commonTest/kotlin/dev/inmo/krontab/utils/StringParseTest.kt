package dev.inmo.krontab.utils

import korlibs.time.*
import dev.inmo.krontab.KronSchedulerTz
import dev.inmo.krontab.buildSchedule
import dev.inmo.krontab.doInfinity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.test.runTest
import kotlin.test.*

@ExperimentalCoroutinesApi
@FlowPreview
class StringParseTest {
    @Test
    fun testThatFlowIsCorrectlyWorkEverySecondBuiltOnString() {
        val kronScheduler = buildSchedule("*/1 * * * *")

        val flow = kronScheduler.asFlowWithoutDelays()

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
    fun testThatFlowIsCorrectlyWorkEverySecondWhenMillisIsHalfOfSecondBuiltOnString() {
        val kronScheduler = buildSchedule("*/1 * * * * 500ms")

        val flow = kronScheduler.asFlowWithoutDelays()

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

        val flow = kronScheduler.asFlowWithoutDelays()

        runTest {
            val testsCount = 10
            val failJob = createFailJob((testsCount * 2) * 1000L)
            val mustBeCollected = 10
            val answers = (0 until testsCount).map { _ ->
                async {
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

        val flow = kronScheduler.asFlowWithoutDelays()

        runTest {
            val ranges = rangesEnds.map { it.first .. it.second }.flatten().distinct().toMutableList()
            val expectedCollects = ranges.size
            var collected = 0

            flow.takeWhile { ranges.isNotEmpty() }.collect {
                ranges.remove(it.seconds)
                collected++
                assertTrue(
                    collected <= expectedCollects,
                    "Expected value should be less than $expectedCollects, but was $collected. Ranges state: $ranges"
                )
            }
            assertEquals(expectedCollects, collected)
        }
    }
    @Test
    fun testNextIsCorrectlyWorkEverySeveralMillisecondsRangeBuiltOnString() {
        val rangesEnds = listOf(0, 200, 500, 750)
        val kronScheduler = buildSchedule("* * * * * ${rangesEnds.joinToString(",") { "$it" }}ms")

        runTest {
            val ranges = rangesEnds.toMutableList()
            val expectedCollects = ranges.size
            var collected = 0

            var currentTime = DateTime.now()
            while (ranges.isNotEmpty()) {
                val nextTrigger = kronScheduler.next(currentTime) ?: error("Strangely unable to get next time")

                ranges.remove(nextTrigger.milliseconds)
                collected++

                currentTime = nextTrigger + 1.milliseconds
            }
            assertEquals(expectedCollects, collected)
        }
    }
    @Test
    fun testThatTimezoneCorrectlyDeserialized() {
        val now = DateTime.now().copy(milliseconds = 0).local

        runTest {
            for (i in 0 .. 1339) {
                val expectedInCurrentOffset = now.toOffset(TimezoneOffset(i.minutes)) + 1.hours
                val kronScheduler = buildSchedule(
                    "${expectedInCurrentOffset.seconds} ${expectedInCurrentOffset.minutes} ${expectedInCurrentOffset.hours} * * ${i}o"
                ) as KronSchedulerTz
                val next = kronScheduler.next(now)
                assertEquals(expectedInCurrentOffset.toOffset(now.offset), next)
            }
        }
    }
}
