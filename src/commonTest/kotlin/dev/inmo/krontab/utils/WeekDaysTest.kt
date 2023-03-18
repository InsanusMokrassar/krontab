package dev.inmo.krontab.utils

import com.soywiz.klock.*
import dev.inmo.krontab.builder.buildSchedule
import kotlinx.coroutines.test.runTest
import kotlin.math.ceil
import kotlin.test.*

class WeekDaysTest {
    @Test
    fun testThatWeekDaysSchedulingWorks() {
        val startDateTime = DateTime.now().startOfDay
        val weekDay = startDateTime.dayOfWeek.index0
        val testDays = 400
        val scheduler = buildSchedule {
            dayOfWeek {
                at(weekDay)
            }
            years {
                at(startDateTime.yearInt)
            }
        }
        runTest {
            for (day in 0 until testDays) {
                val currentDateTime = startDateTime + day.days
                val next = scheduler.next(currentDateTime)
                val expected = when {
                    day % 7 == 0 -> currentDateTime
                    else -> startDateTime + ceil(day.toFloat() / 7).weeks
                }
                if (expected.yearInt != startDateTime.yearInt) {
                    assertNull(next)
                } else {
                    assertEquals(expected, next)
                }
            }
        }
    }
}
