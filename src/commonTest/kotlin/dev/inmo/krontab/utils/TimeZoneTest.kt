package dev.inmo.krontab.utils

import korlibs.time.*
import dev.inmo.krontab.builder.buildSchedule
import dev.inmo.krontab.next
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeZoneTest {
    @Test
    fun testDifferentTimeZonesReturnsDifferentTimes() {
        val scheduler = buildSchedule { seconds { every(1) } }
        val additionalMilliseconds = 100.milliseconds
        val baseDate = DateTime.now().startOfWeek.copy(milliseconds = additionalMilliseconds.millisecondsInt)
        runTest {
            for (i in 0 until 7) {
                val now = baseDate + i.days
                for (j in 0 .. 24) {
                    val nowTz = now.toOffset(j.hours)
                    val next = scheduler.next(nowTz)!!
                    assertEquals(
                        (nowTz + 1.seconds - additionalMilliseconds).utc.unixMillisLong, next.utc.unixMillisLong
                    )
                }
            }
        }
    }
}
