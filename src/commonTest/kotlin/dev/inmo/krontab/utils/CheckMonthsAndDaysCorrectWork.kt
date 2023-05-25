package dev.inmo.krontab.utils

import korlibs.time.DateTime
import korlibs.time.days
import dev.inmo.krontab.buildSchedule
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class CheckMonthsAndDaysCorrectWork {
    @Test
    fun checkMonthsAndDaysCorrectWork() {
        val now = DateTime.now().startOfYear.startOfDay
        for (i in 0 until now.year.days) {
            val scheduleDateTime = (now + i.days)
            runTest {
                assertEquals(
                    scheduleDateTime,
                    buildSchedule("0 0 0 ${scheduleDateTime.dayOfMonth - 1} ${scheduleDateTime.month0}").next(now)
                )
            }
        }
    }
}
