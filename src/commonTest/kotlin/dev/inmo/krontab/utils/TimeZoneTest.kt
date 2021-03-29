package dev.inmo.krontab.utils

import com.soywiz.klock.*
import dev.inmo.krontab.builder.buildSchedule
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeZoneTest {
    @Test
    fun testDifferentTimeZonesReturnsDifferentTimes() {
        val scheduler = buildSchedule { seconds { every(1) } }
        val now = DateTime.now()
        runTest {
            for (i in 0 .. 24) {
                val nowTz = now.toOffset(i.hours)
                val next = scheduler.next(nowTz)!!
                assertEquals(
                    (nowTz + 1.seconds).utc.unixMillisLong, next.utc.unixMillisLong
                )
            }
        }
    }
}