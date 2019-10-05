package com.github.insanusmokrassar.krontab

import com.soywiz.klock.DateTime
import com.soywiz.klock.DateTimeSpan
import kotlinx.coroutines.delay

private val anyCronDateTime by lazy {
    CronDateTime()
}

data class CronDateTimeScheduler internal constructor(
    internal val cronDateTimes: List<CronDateTime>
)

fun CronDateTimeScheduler.next(relatively: DateTime = DateTime.now()): DateTime {
    return cronDateTimes.map { it.toNearDateTime(relatively) }.min() ?: anyCronDateTime.toNearDateTime(relatively)
}

suspend fun CronDateTimeScheduler.doInLoop(block: suspend () -> Boolean) {
    do {
        delay(next().unixMillisLong - DateTime.now().unixMillisLong)
    } while (block())
}
