package dev.inmo.krontab.utils

import com.soywiz.klock.DateTime
import com.soywiz.klock.days
import dev.inmo.krontab.buildSchedule
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
