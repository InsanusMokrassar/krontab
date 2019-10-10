package com.insanusmokrassar.krontab

import com.insanusmokrassar.krontab.internal.*
import com.soywiz.klock.DateTime
import kotlinx.coroutines.delay

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


fun createCronDateTimeScheduler(incoming: String): CronDateTimeScheduler {
    val (secondsSource, minutesSource, hoursSource, dayOfMonthSource, monthSource) = incoming.split(" ")

    val secondsParsed = parseSeconds(secondsSource)
    val minutesParsed = parseMinutes(minutesSource)
    val hoursParsed = parseHours(hoursSource)
    val dayOfMonthParsed = parseDaysOfMonth(dayOfMonthSource)
    val monthParsed = parseMonths(monthSource)

    val resultCronDateTimes = mutableListOf(CronDateTime())

    secondsParsed ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
        previousCronDateTime.copy(seconds = currentTime)
    }

    minutesParsed ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
        previousCronDateTime.copy(minutes = currentTime)
    }

    hoursParsed ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
        previousCronDateTime.copy(hours = currentTime)
    }

    dayOfMonthParsed ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
        previousCronDateTime.copy(dayOfMonth = currentTime)
    }

    monthParsed ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
        previousCronDateTime.copy(month = currentTime)
    }

    return CronDateTimeScheduler(resultCronDateTimes.toList())
}
