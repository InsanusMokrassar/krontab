package com.github.insanusmokrassar.krontab

import com.soywiz.klock.DateTime
import kotlinx.coroutines.delay

private val anyCronDateTime by lazy {
    CronDateTime()
}

data class CronDateTimeScheduler(
    internal val cronDateTimes: List<CronDateTime>
)

internal fun CronDateTimeScheduler.next(relatively: DateTime = DateTime.now()): DateTime {
    return cronDateTimes.map { it.toNearDateTime(relatively) }.min() ?: anyCronDateTime.toNearDateTime(relatively)
}

suspend fun CronDateTimeScheduler.doInLoop(block: () -> Boolean) {
    do {
        delay(next().unixMillisLong - DateTime.now().unixMillisLong)
    } while (block())
}
