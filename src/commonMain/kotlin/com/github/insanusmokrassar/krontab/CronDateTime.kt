package com.github.insanusmokrassar.krontab

import com.soywiz.klock.*
import com.github.insanusmokrassar.krontab.utils.clamp

private val incomeMonthRange = 0 .. 11
private val incomeDayOfMonthRange = 0 .. 31
private val incomeHourRange = 0 .. 23
private val incomeMinuteRange = 0 .. 59
private val incomeSecondRange = 0 .. 59

fun CronDateTime(
    month: Int? = null,
    dayOfMonth: Int? = null,
    hour: Int? = null,
    minute: Int? = null,
    second: Int? = null
) = CronDateTime(
    month ?.clamp(incomeMonthRange) ?.toByte(),
    dayOfMonth ?.clamp(incomeDayOfMonthRange) ?.toByte(),
    hour ?.clamp(incomeHourRange) ?.toByte(),
    minute ?.clamp(incomeMinuteRange) ?.toByte(),
    second ?.clamp(incomeSecondRange) ?.toByte()
)

/**
 * [month] 0-11
 * [dayOfMonth] 0-31
 * [hour] 0-23
 * [minute] 0-59
 * [second] 0-59
 */
data class CronDateTime(
    val month: Byte? = null,
    val dayOfMonth: Byte? = null,
    val hour: Byte? = null,
    val minute: Byte? = null,
    val second: Byte? = null
) {
    init {
        check(month ?.let { it in incomeMonthRange } ?: true)
        check(dayOfMonth ?.let { it in incomeDayOfMonthRange } ?: true)
        check(hour ?.let { it in incomeHourRange } ?: true)
        check(minute ?.let { it in incomeMinuteRange } ?: true)
        check(second ?.let { it in incomeSecondRange } ?: true)
    }

    internal val klockDayOfMonth = dayOfMonth ?.plus(1)
}

fun CronDateTime.toNearDateTime(relativelyTo: DateTime = DateTime.now()): DateTime {
    var current = relativelyTo

    second ?.let {
        val left = it - current.seconds
        current += DateTimeSpan(minutes = if (left < 0) 1 else 0, seconds = left)
    }

    minute ?.let {
        val left = it - current.minutes
        current += DateTimeSpan(hours = if (left < 0) 1 else 0, minutes = left)
    }

    hour ?.let {
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
