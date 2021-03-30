package dev.inmo.krontab.internal

internal val yearRange = Int.MIN_VALUE .. Int.MAX_VALUE
internal val monthRange = 0 .. 11
internal val dayOfMonthRange = 0 .. 30
internal val hoursRange = 0 .. 23
internal val minutesRange = 0 .. 59
internal val secondsRange = minutesRange

/**
 * From 0 - 1439 minutes (1440 == 24 hours, that is the same as 0 in terms of timezones)
 */
internal val offsetRange = 0 until (hoursRange.count() * minutesRange.count())
