package com.insanusmokrassar.krontab.utils

import com.insanusmokrassar.krontab.buildSchedule
import com.insanusmokrassar.krontab.builder.buildSchedule
import com.insanusmokrassar.krontab.createSimpleScheduler
import com.soywiz.klock.DateTime
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile
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
}
