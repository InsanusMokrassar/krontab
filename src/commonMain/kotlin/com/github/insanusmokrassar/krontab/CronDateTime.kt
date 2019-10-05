package com.github.insanusmokrassar.krontab

import com.soywiz.klock.*
import com.github.insanusmokrassar.krontab.utils.clamp

private val incomeMonthRange = 0 .. 11
private val incomeDayOfMonthRange = 0 .. 31
private val incomeHourRange = 0 .. 23
private val incomeMinuteRange = 0 .. 59
private val incomeSecondRange = 0 .. 59

/**
 * [month] 0-11
 * [dayOfMonth] 0-31
 * [hours] 0-23
 * [minutes] 0-59
 * [seconds] 0-59
 */
data class CronDateTime(
    val month: Byte? = null,
    val dayOfMonth: Byte? = null,
    val hours: Byte? = null,
    val minutes: Byte? = null,
    val seconds: Byte? = null
) {
    init {
        check(month ?.let { it in incomeMonthRange } ?: true)
        check(dayOfMonth ?.let { it in incomeDayOfMonthRange } ?: true)
        check(hours?.let { it in incomeHourRange } ?: true)
        check(minutes?.let { it in incomeMinuteRange } ?: true)
        check(seconds?.let { it in incomeSecondRange } ?: true)
    }

    internal val klockDayOfMonth = dayOfMonth ?.plus(1)

    companion object {
        fun create(
            month: Int? = null,
            dayOfMonth: Int? = null,
            hours: Int? = null,
            minutes: Int? = null,
            seconds: Int? = null
        ) = CronDateTime(
            month ?.clamp(incomeMonthRange) ?.toByte(),
            dayOfMonth ?.clamp(incomeDayOfMonthRange) ?.toByte(),
            hours ?.clamp(incomeHourRange) ?.toByte(),
            minutes ?.clamp(incomeMinuteRange) ?.toByte(),
            seconds ?.clamp(incomeSecondRange) ?.toByte()
        )
    }
}

fun CronDateTime.toNearDateTime(relativelyTo: DateTime = DateTime.now()): DateTime {
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
