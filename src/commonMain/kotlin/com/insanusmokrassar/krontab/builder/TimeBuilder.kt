package com.insanusmokrassar.krontab.builder

import com.insanusmokrassar.krontab.internal.*
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

    inline fun from(value: Int) = value

    infix fun Int.every(delay: Int): Array<Int> {
        val progression = clamp(restrictionsRange) .. restrictionsRange.last step delay
        val result = progression.toSet().toTypedArray()

        this@TimeBuilder include result

        return result
    }
    infix fun every(delay: Int): Array<Int> = 0 every delay

    infix fun Int.upTo(endIncluding: Int): Array<Int> {
        val progression = clamp(restrictionsRange) .. endIncluding.clamp(restrictionsRange)
        val result = progression.toSet().toTypedArray()

        this@TimeBuilder include result

        return result
    }
    infix fun upTo(endIncluding: Int): Array<Int> = 0 upTo endIncluding

    internal fun build() = result ?.map { it.toByte() } ?.toTypedArray()
}

class SecondsBuilder : TimeBuilder(secondsRange)
class MinutesBuilder : TimeBuilder(minutesRange)
class HoursBuilder : TimeBuilder(com.insanusmokrassar.krontab.internal.hoursRange)
class DaysOfMonthBuilder : TimeBuilder(com.insanusmokrassar.krontab.internal.dayOfMonthRange)
class MonthsBuilder : TimeBuilder(monthRange)
