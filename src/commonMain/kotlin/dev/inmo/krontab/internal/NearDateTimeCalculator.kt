package dev.inmo.krontab.internal

import com.soywiz.klock.*
import dev.inmo.krontab.utils.copy
import kotlin.math.min

internal class NearDateTimeCalculator<T>(
    private val times: Array<T>,
    private val partGetter: (DateTime) -> T,
    private val partSetter: (DateTime, T) -> DateTime?
) where T : Comparable<T>, T : Number {
    /**
     * @return pair of near [DateTime] for this checker and [Boolean] flag that all previous calculations must be
     * recalculated
     */
    fun calculateNearTime(
        relativelyTo: DateTime
    ): Pair<DateTime, Boolean>? {
        val currentData = partGetter(relativelyTo)
        val greaterOrEquals = times.firstOrNull { it >= currentData }
        val newDateTime = if (greaterOrEquals == null) {
            partSetter(relativelyTo, times.first()) ?: return null
        } else {
            partSetter(relativelyTo, greaterOrEquals) ?: return null
        }
        return if (newDateTime == relativelyTo) {
            relativelyTo to false
        } else {
            newDateTime to true
        }
    }
}

internal fun NearDateTimeCalculatorMillis(
    times: Array<Short>
) = NearDateTimeCalculator(
    times,
    { it.milliseconds.toShort() },
    { dateTime, newOne ->
        (if (newOne < dateTime.milliseconds) {
            dateTime.plus(1.seconds)
        } else {
            dateTime
        }).copy(milliseconds = newOne.toInt())
    }
)

internal fun NearDateTimeCalculatorSeconds(
    times: Array<Byte>
) = NearDateTimeCalculator(
    times,
    { it.seconds.toByte() },
    { dateTime, newOne ->
        (if (newOne < dateTime.seconds) {
            dateTime.plus(1.minutes)
        } else {
            dateTime
        }).copy(second = newOne.toInt(), milliseconds = 0)
    }
)

internal fun NearDateTimeCalculatorMinutes(
    times: Array<Byte>
) = NearDateTimeCalculator(
    times,
    { it.minutes.toByte() },
    { dateTime, newOne ->
        (if (newOne < dateTime.minutes) {
            dateTime.plus(1.hours)
        } else {
            dateTime
        }).copy(minute = newOne.toInt(), second = 0, milliseconds = 0)
    }
)

internal fun NearDateTimeCalculatorHours(
    times: Array<Byte>
) = NearDateTimeCalculator(
    times,
    { it.hours.toByte() },
    { dateTime, newOne ->
        (if (newOne < dateTime.hours) {
            dateTime.plus(1.days)
        } else {
            dateTime
        }).copy(hour = newOne.toInt(), minute = 0, second = 0, milliseconds = 0)
    }
)

internal fun NearDateTimeCalculatorDays(
    times: Array<Byte>
) = NearDateTimeCalculator(
    times,
    { it.dayOfMonth.toByte() },
    { dateTime, newOne ->
        (if (newOne < dateTime.dayOfMonth) {
            dateTime.plus(1.months)
        } else {
            dateTime
        }).copy(
            dayOfMonth = min(dateTime.month.days(dateTime.year), newOne.toInt() + 1), // index1
            hour = 0,
            minute = 0,
            second = 0,
            milliseconds = 0
        )
    }
)

internal fun NearDateTimeCalculatorMonths(
    times: Array<Byte>
) = NearDateTimeCalculator(
    times,
    { it.dayOfMonth.toByte() },
    { dateTime, newOne ->
        (if (newOne < dateTime.month0) {
            dateTime.plus(1.years)
        } else {
            dateTime
        }).copy(
            month = newOne.toInt() + 1, // index1
            dayOfMonth = 1, // index1
            hour = 0,
            minute = 0,
            second = 0,
            milliseconds = 0
        )
    }
)

internal fun NearDateTimeCalculatorWeekDays(
    times: Array<Byte>
) = NearDateTimeCalculator(
    times,
    { it.dayOfWeek.index0.toByte() },
    { dateTime, newOne ->
        val currentDayOfWeek = dateTime.dayOfWeek.index0
        (if (newOne < currentDayOfWeek) {
            dateTime.plus(7.days - (currentDayOfWeek - newOne).days)
        } else {
            dateTime.plus(newOne.toInt().days - currentDayOfWeek.days)
        }).copy(
            hour = 0,
            minute = 0,
            second = 0,
            milliseconds = 0
        )
    }
)

internal fun NearDateTimeCalculatorYears(
    times: Array<Int>
) = NearDateTimeCalculator(
    times,
    { it.yearInt },
    { dateTime, newOne ->
        val currentYear = dateTime.yearInt
        (if (newOne < currentYear) {
            null
        } else {
            dateTime.plus(newOne.years - currentYear.years)
        }) ?.copy(
            month = 1, // index1
            dayOfMonth = 1, // index1
            hour = 0,
            minute = 0,
            second = 0,
            milliseconds = 0
        )
    }
)
