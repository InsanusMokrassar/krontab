package dev.inmo.krontab.internal

import com.soywiz.klock.*
import dev.inmo.krontab.KronScheduler
import dev.inmo.krontab.utils.copy

/**
 * @param daysOfWeek 0-6
 * @param years any int
 * @param months 0-11
 * @param daysOfMonth 0-31
 * @param hours 0-23
 * @param minutes 0-59
 * @param seconds 0-59
 */
internal data class CronDateTime(
    val daysOfWeek: Array<Byte>? = null,
    val years: Array<Int>? = null,
    val months: Array<Byte>? = null,
    val daysOfMonth: Array<Byte>? = null,
    val hours: Array<Byte>? = null,
    val minutes: Array<Byte>? = null,
    val seconds: Array<Byte>? = null
) {
    init {
        check(daysOfWeek ?.all { it in dayOfWeekRange } ?: true)
        check(years?.all { it in yearRange } ?: true)
        check(months?.all { it in monthRange } ?: true)
        check(daysOfMonth ?.all { it in dayOfMonthRange } ?: true)
        check(hours?.all { it in hoursRange } ?: true)
        check(minutes?.all { it in minutesRange } ?: true)
        check(seconds?.all { it in secondsRange } ?: true)
    }

    internal val calculators = listOf(
        years ?.let { NearDateTimeCalculatorYears(it) },
        daysOfWeek ?.let { NearDateTimeCalculatorWeekDays(it) },
        NearDateTimeCalculatorMillis(arrayOf(0)),
        seconds ?.let { NearDateTimeCalculatorSeconds(it) },
        minutes ?.let { NearDateTimeCalculatorMinutes(it) },
        hours ?.let { NearDateTimeCalculatorHours(it) },
        daysOfMonth ?.let { NearDateTimeCalculatorDays(it) },
        months ?.let { NearDateTimeCalculatorMonths(it) },
    )

    internal fun toNearDateTime(relativelyTo: DateTime = DateTime.now()): DateTime? {
        var current = relativelyTo
        whileLoop@while (true) {
            for (calculator in calculators) {
                val (calculated, requireRecalculation) = (calculator ?: continue).calculateNearTime(current) ?: return null
                current = calculated
                if (requireRecalculation) {
                    continue@whileLoop
                }
            }
            return current
        }
    }
}

internal fun createCronDateTime(
    seconds: Array<Byte>? = null,
    minutes: Array<Byte>? = null,
    hours: Array<Byte>? = null,
    dayOfMonth: Array<Byte>? = null,
    month: Array<Byte>? = null,
    years: Array<Int>? = null,
    weekDays: Array<Byte>? = null
): CronDateTime {
    return CronDateTime(weekDays, years, month, dayOfMonth, hours, minutes, seconds)
}

/**
 * @return [KronScheduler] (in fact [CronDateTimeScheduler]) based on incoming data
 */
internal fun createKronScheduler(
    seconds: Array<Byte>? = null,
    minutes: Array<Byte>? = null,
    hours: Array<Byte>? = null,
    dayOfMonth: Array<Byte>? = null,
    month: Array<Byte>? = null,
    years: Array<Int>? = null,
    weekDays: Array<Byte>? = null
): KronScheduler = CronDateTimeScheduler(createCronDateTime(seconds, minutes, hours, dayOfMonth, month, years, weekDays))
/**
 * @return [KronScheduler] (in fact [CronDateTimeScheduler]) based on incoming data
 */
internal fun createKronSchedulerWithOffset(
    seconds: Array<Byte>? = null,
    minutes: Array<Byte>? = null,
    hours: Array<Byte>? = null,
    dayOfMonth: Array<Byte>? = null,
    month: Array<Byte>? = null,
    years: Array<Int>? = null,
    weekDays: Array<Byte>? = null,
    offset: TimezoneOffset
): KronScheduler = CronDateTimeSchedulerTz(createCronDateTime(seconds, minutes, hours, dayOfMonth, month, years, weekDays), offset)

internal fun List<CronDateTime>.merge() = CronDateTime(
    flatMap { it.daysOfWeek ?.toList() ?: emptyList() }.distinct().toTypedArray().takeIf { it.isNotEmpty() },
    flatMap { it.years ?.toList() ?: emptyList() }.distinct().toTypedArray().takeIf { it.isNotEmpty() },
    flatMap { it.months ?.toList() ?: emptyList() }.distinct().toTypedArray().takeIf { it.isNotEmpty() },
    flatMap { it.daysOfMonth ?.toList() ?: emptyList() }.distinct().toTypedArray().takeIf { it.isNotEmpty() },
    flatMap { it.hours ?.toList() ?: emptyList() }.distinct().toTypedArray().takeIf { it.isNotEmpty() },
    flatMap { it.minutes ?.toList() ?: emptyList() }.distinct().toTypedArray().takeIf { it.isNotEmpty() },
    flatMap { it.seconds ?.toList() ?: emptyList() }.distinct().toTypedArray().takeIf { it.isNotEmpty() },
)
