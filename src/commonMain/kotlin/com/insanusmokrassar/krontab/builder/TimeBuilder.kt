package com.insanusmokrassar.krontab.builder

import com.github.insanusmokrassar.krontab.*
import com.github.insanusmokrassar.krontab.minutesRange
import com.github.insanusmokrassar.krontab.monthRange
import com.github.insanusmokrassar.krontab.secondsRange
import com.insanusmokrassar.krontab.utils.clamp

sealed class TimeBuilder (
    private val restrictionsRange: IntRange
) {
    private var result: Set<Int>? = null

    fun allowAll() {
        result = null
    }

    infix fun include(array: Array<Int>) {
        val clamped = array.map { it.clamp(restrictionsRange) } + (result ?: emptySet())
        result = clamped.toSet()
    }

    infix fun at(value: Int) {
        result = (result ?: emptySet()) + value.clamp(restrictionsRange)
    }

    inline infix fun from(value: Int) = value

    infix fun Int.every(delay: Int): Array<Int> {
        val progression = clamp(restrictionsRange) .. restrictionsRange.last step delay
        val result = progression.toSet().toTypedArray()

        this@TimeBuilder include result

        return result
    }

    infix fun Int.upTo(endIncluding: Int): Array<Int> {
        val progression = clamp(restrictionsRange) .. endIncluding.clamp(restrictionsRange)
        val result = progression.toSet().toTypedArray()

        this@TimeBuilder include result

        return result
    }

    internal fun build() = result ?.map { it.toByte() } ?.toTypedArray()
}

class SecondsBuilder : TimeBuilder(secondsRange)
class MinutesBuilder : TimeBuilder(minutesRange)
class HoursBuilder : TimeBuilder(hoursRange)
class DaysOfMonthBuilder : TimeBuilder(dayOfMonthRange)
class MonthsBuilder : TimeBuilder(monthRange)
