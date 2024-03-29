package dev.inmo.krontab.builder

import dev.inmo.krontab.internal.*

/**
 * This class was created for incapsulation of builder work with specified [restrictionsRange]. For example,
 * [include] function of [TimeBuilder] will always [coerceIn] incoming data using its [restrictionsRange]
 */
sealed class TimeBuilder<T : Number> (
    private val restrictionsRange: IntRange,
    private val converter: Converter<T>
) {
    private var result: Set<Int>? = null

    /**
     * The first possible value of builder
     */
    val first
        get() = restrictionsRange.first
    /**
     * The last possible value of builder. Using of this variable equal to using "L" in strings
     */
    val last
        get() = restrictionsRange.last

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
        val clamped = array.map { it.coerceIn(restrictionsRange) } + (result ?: emptySet())
        result = clamped.toSet()
    }

    /**
     * Add one [value] to current timeline
     */
    @Suppress("unused")
    infix fun at(value: Int) {
        result = (result ?: emptySet()) + value.coerceIn(restrictionsRange)
    }


    /**
     * Shortcut for [at]. In fact will
     */
    @Suppress("unused", "NOTHING_TO_INLINE")
    inline infix fun each(value: Int) = at(value)

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
        val progression = coerceIn(restrictionsRange) .. restrictionsRange.last step delay
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
        val progression = coerceIn(restrictionsRange) .. endIncluding.coerceIn(restrictionsRange)
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

    /**
     * Will include the last possible value
     */
    fun includeLast() = at(restrictionsRange.last)
    /**
     * Will include the first possible value
     */
    fun includeFirst() = at(restrictionsRange.first)

    internal fun build() = result ?.map(converter)
}

class MillisecondsBuilder : TimeBuilder<Short>(millisecondsRange, intToShortConverter)
class SecondsBuilder : TimeBuilder<Byte>(secondsRange, intToByteConverter)
class MinutesBuilder : TimeBuilder<Byte>(minutesRange, intToByteConverter)
class HoursBuilder : TimeBuilder<Byte>(hoursRange, intToByteConverter)
class DaysOfMonthBuilder : TimeBuilder<Byte>(dayOfMonthRange, intToByteConverter)
class MonthsBuilder : TimeBuilder<Byte>(monthRange, intToByteConverter)
class YearsBuilder : TimeBuilder<Int>(yearRange, intToIntConverter)
class WeekDaysBuilder : TimeBuilder<Byte>(dayOfWeekRange, intToByteConverter)
