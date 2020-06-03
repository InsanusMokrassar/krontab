package com.insanusmokrassar.krontab.internal

import com.insanusmokrassar.krontab.utils.clamp
import com.soywiz.klock.DateTime
import com.soywiz.klock.DateTimeSpan

/**
 * [month] 0-11
 * [dayOfMonth] 0-31
 * [hours] 0-23
 * [minutes] 0-59
 * [seconds] 0-59
 */
internal data class CronDateTime(
    val month: Byte? = null,
    val dayOfMonth: Byte? = null,
    val hours: Byte? = null,
    val minutes: Byte? = null,
    val seconds: Byte? = null
) {
    init {
        check(month ?.let { it in monthRange } ?: true)
        check(dayOfMonth ?.let { it in dayOfMonthRange } ?: true)
        check(hours?.let { it in hoursRange } ?: true)
        check(minutes?.let { it in minutesRange } ?: true)
        check(seconds?.let { it in secondsRange } ?: true)
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
            month ?.clamp(monthRange) ?.toByte(),
            dayOfMonth ?.clamp(dayOfMonthRange) ?.toByte(),
            hours ?.clamp(hoursRange) ?.toByte(),
            minutes ?.clamp(minutesRange) ?.toByte(),
            seconds ?.clamp(secondsRange) ?.toByte()
        )
    }
}

internal fun CronDateTime.toNearDateTime(relativelyTo: DateTime = DateTime.now()): DateTime {
    var current = relativelyTo

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
        val left = it - current.dayOfMonth
        current += DateTimeSpan(months = if (left < 0) 1 else 0, days = left)
    }

    month ?.let {
        val left = it - current.month0
        current += DateTimeSpan(months = if (left < 0) 1 else 0, days = left)
    }

    return current
}
