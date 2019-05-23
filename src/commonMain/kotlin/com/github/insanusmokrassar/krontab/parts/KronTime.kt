package com.github.insanusmokrassar.krontab.parts

import kotlin.math.floor

private const val millisCount = 1000
private const val secondsCount = 60
private const val minutesCount = 60
private const val hoursCount = 24
private const val weekDaysCount = 7

private const val secondsK = 1000
private const val minutesK = 60 * secondsK
private const val hoursK = 60 * minutesK
private const val daysK = 24 * hoursK
private const val weekDaysK = 7 * daysK

private fun <T> List<T>.notMaxOrNull(max: Int): List<T>? = if (size >= max) {
    null
} else {
    this
}

internal fun KronTimes(
    timePattern: TimePattern
): List<KronTime> {
    val reversedParts = timePattern.split(" ").toMutableList().also {
        it.reverse()
    }
    val weekDaysParts = getTimes(reversedParts.removeAt(0), 0, weekDaysCount).notMaxOrNull(weekDaysCount) ?.map {
        it.toByte()
    }
    val hoursParts = getTimes(reversedParts.removeAt(0), 0, hoursCount).let {
        (weekDaysParts ?.let { _ ->
            it
        } ?: it.notMaxOrNull(hoursCount)) ?.map {
            it.toByte()
        }
    }
    val minutesParts = getTimes(reversedParts.removeAt(0), 0, minutesCount).let {
        (hoursParts ?.let { _ ->
            it
        } ?: it.notMaxOrNull(minutesCount)) ?.map {
            it.toByte()
        }
    }
    val secondsParts = getTimes(reversedParts.removeAt(0), 0, secondsCount).let {
        (minutesParts ?.let { _ ->
            it
        } ?: it.notMaxOrNull(secondsCount)) ?.map {
            it.toByte()
        }
    }
    val millisParts = if (reversedParts.isNotEmpty()) {
        getTimes(reversedParts.removeAt(0), 0, millisCount).let {
            secondsParts ?.let { _ ->
                it
            } ?: it.notMaxOrNull(millisCount)
        } ?.map {
            it.toShort()
        }
    } else {
        null
    }

    return millisParts ?.flatMap { millis ->
        secondsParts ?.flatMap { seconds ->
            minutesParts ?.flatMap { minutes ->
                hoursParts ?.flatMap { hours ->
                    weekDaysParts ?.map { weekDay ->
                        KronTime(millis, seconds, minutes, hours, weekDay)
                    } ?: listOf(KronTime(millis, seconds, minutes, hours, null))
                } ?: listOf(KronTime(millis, seconds, minutes, null, null))
            } ?: listOf(KronTime(millis, seconds, null, null, null))
        } ?: listOf(KronTime(millis, null, null, null, null))
    } ?: listOf(KronTime(null, null, null, null, null))
}

internal fun KronTime(
    milliseconds: Milliseconds
): KronTime {
    val millis = (milliseconds % millisCount).toShort()
    var currentDivided: Double = floor(milliseconds.toDouble() / millisCount)

    val seconds: Byte = (currentDivided % secondsCount).toByte()
    currentDivided = floor(currentDivided / secondsCount)

    val minutes: Byte = (currentDivided % minutesCount).toByte()
    currentDivided = floor(currentDivided / minutesCount)

    val hours: Byte = (currentDivided % hoursCount).toByte()
    currentDivided = floor(currentDivided / hoursCount)

    val days: Byte = (currentDivided % weekDaysCount).toByte()

    return KronTime(
        millis,
        seconds,
        minutes,
        hours,
        days
    )
}

internal data class KronTime(
    val milliseconds: Milliseconds?,
    val seconds: Seconds?,
    val minutes: Minutes?,
    val hours: Hours?,
    val weekDays: WeekDays?
)
