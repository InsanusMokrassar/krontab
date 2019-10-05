package com.github.insanusmokrassar.krontab

import com.soywiz.klock.DateTime
import com.soywiz.klock.DateTimeSpan
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

internal suspend fun CronDateTimeScheduler.doInLoop(block: suspend () -> Boolean) {
    do {
        delay(next().unixMillisLong - DateTime.now().unixMillisLong)
    } while (block())
}

internal fun CronDateTime.toNearDateTime(relativelyTo: DateTime = DateTime.now()): DateTime {
    var current = relativelyTo

    seconds?.let {
        val left = it - current.seconds
        current += DateTimeSpan(minutes = if (left < 0) 1 else 0, seconds = left)
    }

    minutes?.let {
        val left = it - current.minutes
        current += DateTimeSpan(hours = if (left < 0) 1 else 0, minutes = left)
    }

    hours?.let {
        val left = it - current.hours
        current += DateTimeSpan(days = if (left < 0) 1 else 0, hours = left)
    }

    klockDayOfMonth ?.let {
        val left = it - current.dayOfMonth
        current += DateTimeSpan(months = if (left < 0) 1 else 0, days = left)
    }

    month ?.let {
        val left = it - current.month0
        current += DateTimeSpan(months = if (left < 0) 1 else 0, days = left)
    }

    return current
}
