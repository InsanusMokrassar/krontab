package dev.inmo.krontab

import com.soywiz.klock.TimezoneOffset
import com.soywiz.klock.minutes
import dev.inmo.krontab.internal.*
import dev.inmo.krontab.utils.Minutes

/**
 * @see createSimpleScheduler
 * @see buildSchedule
 */
typealias KrontabTemplate = String

/**
 * Parse [incoming] string and adapt according to next format: "* * * * *" where order of things:
 *
 * * seconds
 * * minutes
 * * hours
 * * dayOfMonth
 * * month
 * * (optional) year
 * * (optional) (can be placed anywhere after month) (must be marked with `o` at the end, for example: 60o == +01:00) offset
 * * (optional) (can be placed anywhere after month) dayOfWeek
 *
 * And each one (except of offsets) have next format:
 *
 * `{number}[,{number},...]` or `*`
 *
 * and {number} here is one of
 *
 * * {int}-{int}
 * * {int}/{int}
 * * *&#47;{int}
 * * {int}
 * * F
 * * L
 *
 * Week days must be marked with `w` at the end, and starts with 0 which means Sunday. For example, 0w == Sunday. With
 * weeks you can use syntax like with any number like seconds, for example: 0-2w means Sunday-Tuesday
 *
 * Additional info about ranges can be found in follow accordance:
 *
 * * Seconds ranges can be found in [secondsRange]
 * * Minutes ranges can be found in [minutesRange]
 * * Hours ranges can be found in [hoursRange]
 * * Days of month ranges can be found in [dayOfMonthRange]
 * * Months ranges can be found in [monthRange]
 * * Years ranges can be found in [yearRange] (in fact - any [Int])
 * * WeekDay (timezone) ranges can be found in [dayOfWeekRange]
 *
 * Examples:
 *
 * * "0/5 * * * *" for every five seconds triggering
 * * "0/5,L * * * *" for every five seconds triggering and on 59 second
 * * "0/15 30 * * *" for every 15th seconds in a half of each hour
 * * "1 2 3 F,4,L 5" for triggering in near first second of second minute of third hour of fourth day of may
 * * "1 2 3 F,4,L 5 60o" for triggering in near first second of second minute of third hour of fourth day of may with timezone UTC+01:00
 * * "1 2 3 F,4,L 5 60o 0-2w" for triggering in near first second of second minute of third hour of fourth day of may in case if it will be in Sunday-Tuesday week days with timezone UTC+01:00
 * * "1 2 3 F,4,L 5 2021" for triggering in near first second of second minute of third hour of fourth day of may of 2021st year
 * * "1 2 3 F,4,L 5 2021 60o" for triggering in near first second of second minute of third hour of fourth day of may of 2021st year with timezone UTC+01:00
 * * "1 2 3 F,4,L 5 2021 60o 0-2w" for triggering in near first second of second minute of third hour of fourth day of may of 2021st year if it will be in Sunday-Tuesday week days with timezone UTC+01:00
 *
 * @return In case when offset parameter is absent in [incoming] will be used [createSimpleScheduler] method and
 * returned [CronDateTimeScheduler]. In case when offset parameter there is in [incoming] [KrontabTemplate] will be used
 * [createKronSchedulerWithOffset] and returned [CronDateTimeSchedulerTz]
 *
 * @see dev.inmo.krontab.internal.createKronScheduler
 */
fun createSimpleScheduler(
    incoming: KrontabTemplate
): KronScheduler {
    var offsetParsed: Int? = null
    var dayOfWeekParsed: Array<Byte>? = null
    var yearParsed: Array<Int>? = null
    val (secondsSource, minutesSource, hoursSource, dayOfMonthSource, monthSource) = incoming.split(" ").also {
        listOfNotNull(
            it.getOrNull(5),
            it.getOrNull(6),
            it.getOrNull(7)
        ).forEach {
            val offsetFromString = parseOffset(it)
            val dayOfWeekFromString = parseWeekDay(it)
            offsetParsed = offsetParsed ?: offsetFromString
            dayOfWeekParsed = dayOfWeekParsed ?: dayOfWeekFromString
            when {
                dayOfWeekFromString != null -> return@forEach
                offsetFromString == null && yearParsed == null -> {
                    yearParsed = parseYears(it)
                }
                offsetFromString != null && offsetParsed == null -> {
                    offsetParsed = offsetFromString
                }
            }
        }
    }

    val secondsParsed = parseSeconds(secondsSource)
    val minutesParsed = parseMinutes(minutesSource)
    val hoursParsed = parseHours(hoursSource)
    val dayOfMonthParsed = parseDaysOfMonth(dayOfMonthSource)
    val monthParsed = parseMonths(monthSource)

    return offsetParsed ?.let { offset ->
        createKronSchedulerWithOffset(
            secondsParsed, minutesParsed, hoursParsed, dayOfMonthParsed, monthParsed, yearParsed, dayOfWeekParsed, TimezoneOffset(offset.minutes)
        )
    } ?: createKronScheduler(
        secondsParsed, minutesParsed, hoursParsed, dayOfMonthParsed, monthParsed, yearParsed, dayOfWeekParsed
    )
}

fun createSimpleScheduler(
    incoming: KrontabTemplate,
    defaultOffset: Minutes
): KronSchedulerTz {
    val scheduler = createSimpleScheduler(incoming)
    return if (scheduler is KronSchedulerTz) {
        scheduler
    } else {
        CronDateTimeSchedulerTz(
            (scheduler as CronDateTimeScheduler).cronDateTimes,
            TimezoneOffset(defaultOffset.minutes)
        )
    }
}

/**
 * Shortcut for [createSimpleScheduler]
 */
fun buildSchedule(incoming: KrontabTemplate): KronScheduler = createSimpleScheduler(incoming)
/**
 * Shortcut for [createSimpleScheduler]
 */
fun buildSchedule(incoming: KrontabTemplate, defaultOffset: Minutes): KronSchedulerTz = createSimpleScheduler(incoming, defaultOffset)

/**
 * Shortcut for [buildSchedule]
 */
fun KrontabTemplate.toSchedule(): KronScheduler = buildSchedule(this)
/**
 * Shortcut for [buildSchedule]
 */
fun KrontabTemplate.toSchedule(defaultOffset: Minutes): KronSchedulerTz = buildSchedule(this, defaultOffset)

/**
 * Shortcut for [buildSchedule]
 */
fun KrontabTemplate.toKronScheduler(): KronScheduler = buildSchedule(this)
/**
 * Shortcut for [buildSchedule]
 */
fun KrontabTemplate.toKronScheduler(defaultOffset: Minutes): KronSchedulerTz = buildSchedule(this, defaultOffset)