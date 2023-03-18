package dev.inmo.krontab.utils

import dev.inmo.krontab.builder.buildSchedule
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
@FlowPreview
class SchedulerFlowTests {
    @Test
    fun testThatFlowIsCorrectlyWorkEverySecond() {
        val kronScheduler = buildSchedule {
            seconds {
                0 every 1
            }
        }

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
    fun testThatFlowIsCorrectlyWorkEverySecondWithMuchOfEmitters() {
        val kronScheduler = buildSchedule {
            seconds {
                0 every 1
            }
        }

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
}
