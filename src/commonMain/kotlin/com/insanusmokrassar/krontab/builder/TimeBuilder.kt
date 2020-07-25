package com.insanusmokrassar.krontab.builder

import com.insanusmokrassar.krontab.internal.*
import com.insanusmokrassar.krontab.utils.clamp

/**
 * This class was created for incapsulation of builder work with specified [restrictionsRange]. For example,
 * [include] function of [TimeBuilder] will always [clamp] incoming data using its [restrictionsRange]
 */
sealed class TimeBuilder (
    private val restrictionsRange: IntRange
) {
    private var result: Set<Int>? = null

    /**
     * After calling of this function this builder will allow any value of current time
     */
    @Suppress("unused")
    fun allowAll() {
        result = null
    }

    /**
     * Will include all variations from this array inside of this timeline
     */
    @Suppress("MemberVisibilityCanBePrivate")
    infix fun include(array: Array<Int>) {
        val clamped = array.map { it.clamp(restrictionsRange) } + (result ?: emptySet())
        result = clamped.toSet()
    }

    /**
     * Add one [value] to current timeline
     */
    @Suppress("unused")
    infix fun at(value: Int) {
        result = (result ?: emptySet()) + value.clamp(restrictionsRange)
    }

    /**
     * Just wrapper for more obvious writing something like "[from] 2 [every] 5". For example, for [SecondsBuilder] it
     * will mean "[from] second second [every] 5 seconds", or "2, 7, 13, ..."
     */
    @Suppress("NOTHING_TO_INLINE")
    inline infix fun from(value: Int) = value

    /**
     * Will create an sequence of times starting [from] [this] [every] [delay] times. For example, for [SecondsBuilder] it
     * will mean "[from] second second [every] 5 seconds", or "2, 7, 13, ..."
     *
     * @see [from]
     */
    infix fun Int.every(delay: Int): Array<Int> {
        val progression = clamp(restrictionsRange) .. restrictionsRange.last step delay
        val result = progression.toSet().toTypedArray()

        this@TimeBuilder include result

        return result
    }

    /**
     * Shortcut for "[from] 0 [every] [delay]"
     */
    infix fun every(delay: Int): Array<Int> = this from 0 every delay

    /**
     * Will fill up this timeline from [this] up to [endIncluding]
     */
    @Suppress("MemberVisibilityCanBePrivate")
    infix fun Int.upTo(endIncluding: Int): Array<Int> {
        val progression = clamp(restrictionsRange) .. endIncluding.clamp(restrictionsRange)
        val result = progression.toSet().toTypedArray()

        this@TimeBuilder include result

        return result
    }
    /**
     * Shortcut for "[from] 0 [upTo] [endIncluding]"
     */
    @Suppress("unused")
    infix fun upTo(endIncluding: Int): Array<Int> = this from 0 upTo endIncluding
    /**
     * Will fill up this timeline from [this] up to [endIncluding]
     */
    @Suppress("MemberVisibilityCanBePrivate")
    infix operator fun Int.rangeTo(endIncluding: Int) = upTo(endIncluding)
    /**
     * Shortcut for "[from] 0 [rangeTo] [endIncluding]"
     */
    @Suppress("MemberVisibilityCanBePrivate")
    infix operator fun rangeTo(endIncluding: Int) = (this from 0) rangeTo endIncluding

    internal fun build() = result ?.map { it.toByte() } ?.toTypedArray()
}

class SecondsBuilder : TimeBuilder(secondsRange)
class MinutesBuilder : TimeBuilder(minutesRange)
class HoursBuilder : TimeBuilder(hoursRange)
class DaysOfMonthBuilder : TimeBuilder(dayOfMonthRange)
class MonthsBuilder : TimeBuilder(monthRange)
