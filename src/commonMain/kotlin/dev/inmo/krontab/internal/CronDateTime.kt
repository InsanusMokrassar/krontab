package dev.inmo.krontab.internal

import com.soywiz.klock.*
import dev.inmo.krontab.KronScheduler

/**
 * @param dayOfweek 0-6
 * @param year any int
 * @param month 0-11
 * @param dayOfMonth 0-31
 * @param hours 0-23
 * @param minutes 0-59
 * @param seconds 0-59
 */
internal data class CronDateTime(
    val dayOfweek: Byte? = null,
    val year: Int? = null,
    val month: Byte? = null,
    val dayOfMonth: Byte? = null,
    val hours: Byte? = null,
    val minutes: Byte? = null,
    val seconds: Byte? = null
) {
    init {
        check(dayOfweek ?.let { it in dayOfWeekRange } ?: true)
        check(year ?.let { it in yearRange } ?: true)
        check(month ?.let { it in monthRange } ?: true)
        check(dayOfMonth ?.let { it in dayOfMonthRange } ?: true)
        check(hours?.let { it in hoursRange } ?: true)
        check(minutes?.let { it in minutesRange } ?: true)
        check(seconds?.let { it in secondsRange } ?: true)
    }

    internal val klockDayOfMonth = dayOfMonth ?.plus(1)
    internal val dayOfWeekInt: Int? = dayOfweek ?.toInt()
}

/**
 * THIS METHOD WILL <b>NOT</b> TAKE CARE ABOUT [offset] PARAMETER. It was decided due to the fact that we unable to get
 * real timezone offset from simple [DateTime]
 *
 * @return The near [DateTime] which happens after [relativelyTo] or will be equal to [relativelyTo]
 */
internal fun CronDateTime.toNearDateTime(relativelyTo: DateTime = DateTime.now()): DateTime? {
    var current = relativelyTo

    val weekDay = dayOfWeekInt
    if (weekDay != null && current.dayOfWeek.index0 != weekDay) {
        do {
            var diff = weekDay - current.dayOfWeek.index0
            if (diff < 0) {
                diff += 7 /* days in week */
            }
            current = (current + diff.days).startOfDay

            val next = toNearDateTime(current)
            if (next ?.dayOfWeek ?.index0 == weekDay) {
                return next
            }
        } while (true)
    }

    seconds?.let {
        val left = it - current.seconds
        current += DateTimeSpan(minutes = if (left <= 0) 1 else 0, seconds = left)
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
        val left = (it - current.dayOfMonth).let { diff ->
            if (diff > 0 && current.endOfMonth.run { it > dayOfMonth && current.dayOfMonth == dayOfMonth }) {
                0
            } else {
                diff
            }
        }
        current += DateTimeSpan(months = if (left < 0) 1 else 0, days = left)
    }

    month ?.let {
        val left = it - current.month0
        current += DateTimeSpan(years = if (left < 0) 1 else 0, months = left)
    }

    year ?.let {
        if (current.yearInt != it) {
            return null
        }
    }

    return current
}

internal fun createCronDateTimeList(
    seconds: Array<Byte>? = null,
    minutes: Array<Byte>? = null,
    hours: Array<Byte>? = null,
    dayOfMonth: Array<Byte>? = null,
    month: Array<Byte>? = null,
    years: Array<Int>? = null,
    weekDays: Array<Byte>? = null
): List<CronDateTime> {
    val resultCronDateTimes = mutableListOf(CronDateTime())

    seconds ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
        previousCronDateTime.copy(seconds = currentTime)
    }

    minutes ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
        previousCronDateTime.copy(minutes = currentTime)
    }

    hours ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
        previousCronDateTime.copy(hours = currentTime)
    }

    dayOfMonth ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
        previousCronDateTime.copy(dayOfMonth = currentTime)
    }

    month ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
        previousCronDateTime.copy(month = currentTime)
    }

    years ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Int ->
        previousCronDateTime.copy(year = currentTime)
    }

    weekDays ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
        previousCronDateTime.copy(dayOfweek = currentTime)
    }

    return resultCronDateTimes.toList()
}

/**
 * @return [KronScheduler] (in fact [CronDateTimeScheduler]) based on incoming data
 */
internal fun createKronScheduler(
    seconds: Array<Byte>? = null,
    minutes: Array<Byte>? = null,
    hours: Array<Byte>? = null,
    dayOfMonth: Array<Byte>? = null,
    month: Array<Byte>? = null,
    years: Array<Int>? = null,
    weekDays: Array<Byte>? = null
): KronScheduler = CronDateTimeScheduler(createCronDateTimeList(seconds, minutes, hours, dayOfMonth, month, years, weekDays))
/**
 * @return [KronScheduler] (in fact [CronDateTimeScheduler]) based on incoming data
 */
internal fun createKronSchedulerWithOffset(
    seconds: Array<Byte>? = null,
    minutes: Array<Byte>? = null,
    hours: Array<Byte>? = null,
    dayOfMonth: Array<Byte>? = null,
    month: Array<Byte>? = null,
    years: Array<Int>? = null,
    weekDays: Array<Byte>? = null,
    offset: TimezoneOffset
): KronScheduler = CronDateTimeSchedulerTz(createCronDateTimeList(seconds, minutes, hours, dayOfMonth, month, years, weekDays), offset)
