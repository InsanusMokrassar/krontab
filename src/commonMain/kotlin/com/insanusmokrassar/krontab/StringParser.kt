package com.insanusmokrassar.krontab

import com.insanusmokrassar.krontab.internal.*

/**
 * Parse [incoming] string and adapt according to next format: "* * * * *" where order of things:
 *
 * seconds
 * minutes
 * hours
 * dayOfMonth
 * month
 *
 * And each one have next format:
 *
 * {number},{number},...
 *
 * and {number} here is one of {int}-{int} OR {int}/{int} OR *\/{int} OR {int}.
 *
 * Seconds ranges can be found in [com.insanusmokrassar.krontab.internal.secondsRange].
 * Minutes ranges can be found in [com.insanusmokrassar.krontab.internal.minutesRange].
 * Hours ranges can be found in [com.insanusmokrassar.krontab.internal.hoursRange].
 * Days of month ranges can be found in [com.insanusmokrassar.krontab.internal.dayOfMonthRange].
 * Months ranges can be found in [com.insanusmokrassar.krontab.internal.monthRange].
 *
 * @sample "0/5 * * * *" for every five seconds triggering
 * @sample "0/15 30 * * *" for every 15th seconds in a half of each hour
 * @sample "1 2 3 4 5" for triggering in near first second of second minute of third hour of fourth day of may
 */
fun createSimpleScheduler(incoming: String): KronScheduler {
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