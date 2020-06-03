package com.insanusmokrassar.krontab

import com.insanusmokrassar.krontab.internal.*

/**
 * Parse [incoming] string and adapt according to next format: "* * * * *" where order of things:
 *
 * * seconds
 * * minutes
 * * hours
 * * dayOfMonth
 * * month
 *
 * And each one have next format:
 *
 * `{number},{number},...`
 *
 * and {number} here is one of
 *
 * * {int}-{int}
 * * {int}/{int}
 * * *&#47;{int}
 * * {int}
 *
 * Additional info about ranges can be found in follow accordance:
 *
 * * Seconds ranges can be found in [secondsRange]
 * * Minutes ranges can be found in [minutesRange]
 * * Hours ranges can be found in [hoursRange]
 * * Days of month ranges can be found in [dayOfMonthRange]
 * * Months ranges can be found in [monthRange]
 *
 * Examples:
 *
 * * "0/5 * * * *" for every five seconds triggering
 * * "0/15 30 * * *" for every 15th seconds in a half of each hour
 * * "1 2 3 4 5" for triggering in near first second of second minute of third hour of fourth day of may
 *
 * @see com.insanusmokrassar.krontab.internal.createKronScheduler
 */
fun createSimpleScheduler(incoming: String): KronScheduler {
    val (secondsSource, minutesSource, hoursSource, dayOfMonthSource, monthSource) = incoming.split(" ")

    val secondsParsed = parseSeconds(secondsSource)
    val minutesParsed = parseMinutes(minutesSource)
    val hoursParsed = parseHours(hoursSource)
    val dayOfMonthParsed = parseDaysOfMonth(dayOfMonthSource)
    val monthParsed = parseMonths(monthSource)

    return createKronScheduler(
        secondsParsed, minutesParsed, hoursParsed, dayOfMonthParsed, monthParsed
    )
}

/**
 *
 */
fun buildSchedule(incoming: String): KronScheduler = createSimpleScheduler(incoming)