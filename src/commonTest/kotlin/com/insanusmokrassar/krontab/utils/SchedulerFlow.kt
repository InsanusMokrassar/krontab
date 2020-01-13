package com.insanusmokrassar.krontab.utils

import com.insanusmokrassar.krontab.builder.buildSchedule
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile
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
}
