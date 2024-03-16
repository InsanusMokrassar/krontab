package dev.inmo.krontab.utils

import dev.inmo.krontab.*
import korlibs.time.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class BuildersTest {
    @Test
    fun presetsWorksCorrectly() {
        val data = mapOf(
            EverySecondScheduler to { it: DateTime -> if (it.milliseconds > 0 ) it + 1.seconds - it.milliseconds.milliseconds else it },
            EveryMinuteScheduler to { it: DateTime -> if (it.seconds > 0 || it.milliseconds > 0 ) it + 1.minutes - it.seconds.seconds - it.milliseconds.milliseconds else it },
            EveryHourScheduler to { it: DateTime -> if (it.minutes > 0 || it.seconds > 0 || it.milliseconds > 0 ) it + 1.hours - it.minutes.minutes - it.seconds.seconds - it.milliseconds.milliseconds else it },
            EveryDayOfMonthScheduler to { it: DateTime -> if (it.hours > 0 || it.minutes > 0 || it.seconds > 0 || it.milliseconds > 0 ) it + 1.days - it.hours.hours - it.minutes.minutes - it.seconds.seconds - it.milliseconds.milliseconds else it },
            EveryMonthScheduler to { it: DateTime -> if (it.dayOfMonth > 1 || it.hours > 0 || it.minutes > 0 || it.seconds > 0 || it.milliseconds > 0 ) (it + 1.months).copy(dayOfMonth = 1, hour = 0, minute = 0, second = 0, milliseconds = 0) else it },
        )
        val samples = 10000

        runTest {
            var now = DateTime.now()
            for (i in 0 until samples) {
                data.forEach { (scheduler, expectCalculator) ->
                    val expectValue = expectCalculator(now)
                    val newNow = scheduler.nextOrRelative(now)

                    assertEquals(expectValue, newNow, "For time ${now.toStringDefault()} calculated wrong value: ${newNow.toStringDefault()} is not equal to ${expectValue.toStringDefault()}")

                    now = newNow
                }
            }
        }
    }
}