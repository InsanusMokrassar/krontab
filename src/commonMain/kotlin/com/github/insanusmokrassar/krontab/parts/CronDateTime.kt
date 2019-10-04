package com.github.insanusmokrassar.krontab.parts

import com.soywiz.klock.*

data class CronDateTime(
    val month: Byte? = null,
    val dayOfMonth: Byte? = null,
    val hour: Byte? = null,
    val minute: Byte? = null,
    val second: Byte? = null
)

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

    dayOfMonth ?.let {
        val left = it - current.dayOfMonth
        current += DateTimeSpan(months = if (left < 0) 1 else 0, days = left)
    }

    month ?.let {
        val left = it - current.month0
        current += DateTimeSpan(months = if (left < 0) 1 else 0, days = left)
    }

    return current
}
