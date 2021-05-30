package dev.inmo.krontab.internal

import com.soywiz.klock.*
import dev.inmo.krontab.KronScheduler

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
        NearDateTimeCalculatorMillis(arrayOf(0)),
        seconds ?.let { NearDateTimeCalculatorSeconds(it) },
        minutes ?.let { NearDateTimeCalculatorMinutes(it) },
        hours ?.let { NearDateTimeCalculatorHours(it) },
        daysOfMonth ?.let { NearDateTimeCalculatorDays(it) },
        months ?.let { NearDateTimeCalculatorMonths(it) },
        years ?.let { NearDateTimeCalculatorYears(it) },
        daysOfWeek ?.let { NearDateTimeCalculatorWeekDays(it) },
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
//
///**
// * THIS METHOD WILL <b>NOT</b> TAKE CARE ABOUT [offset] PARAMETER. It was decided due to the fact that we unable to get
// * real timezone offset from simple [DateTime]
// *
// * @return The near [DateTime] which happens after [relativelyTo] or will be equal to [relativelyTo]
// */
//internal fun CronDateTime.toNearDateTime(relativelyTo: DateTime = DateTime.now()): DateTime? {
//    var current = relativelyTo
//
//    val weekDay = dayOfWeekInt
//    if (weekDay != null && current.dayOfWeek.index0 != weekDay) {
//        do {
//            var diff = weekDay - current.dayOfWeek.index0
//            if (diff < 0) {
//                diff += 7 /* days in week */
//            }
//            current = (current + diff.days).startOfDay
//
//            val next = toNearDateTime(current)
//            if (next == null || next.dayOfWeek.index0 == weekDay) {
//                return next
//            }
//        } while (true)
//    }
//
//    seconds?.let {
//        val left = it - current.seconds
//        current += DateTimeSpan(minutes = if (left <= 0) 1 else 0, seconds = left)
//    }
//
//    minutes?.let {
//        val left = it - current.minutes
//        current += DateTimeSpan(hours = if (left < 0) 1 else 0, minutes = left)
//    }
//
//    hours?.let {
//        val left = it - current.hours
//        current += DateTimeSpan(days = if (left < 0) 1 else 0, hours = left)
//    }
//
//    klockDayOfMonth ?.let {
//        val left = (it - current.dayOfMonth).let { diff ->
//            if (diff > 0 && current.endOfMonth.run { it > dayOfMonth && current.dayOfMonth == dayOfMonth }) {
//                0
//            } else {
//                diff
//            }
//        }
//        current += DateTimeSpan(months = if (left < 0) 1 else 0, days = left)
//    }
//
//    months?.let {
//        val left = it - current.month0
//        current += DateTimeSpan(years = if (left < 0) 1 else 0, months = left)
//    }
//
//    years?.let {
//        if (current.yearInt != it) {
//            return null
//        }
//    }
//
//    return current
//}

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
//    val resultCronDateTimes = mutableListOf(CronDateTime())
//
//    seconds ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
//        previousCronDateTime.copy(seconds = currentTime)
//    }
//
//    minutes ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
//        previousCronDateTime.copy(minutes = currentTime)
//    }
//
//    hours ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
//        previousCronDateTime.copy(hours = currentTime)
//    }
//
//    dayOfMonth ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
//        previousCronDateTime.copy(daysOfMonth = currentTime)
//    }
//
//    month ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
//        previousCronDateTime.copy(months = currentTime)
//    }
//
//    years ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Int ->
//        previousCronDateTime.copy(years = currentTime)
//    }
//
//    weekDays ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
//        previousCronDateTime.copy(daysOfWeek = currentTime)
//    }
//
//    return resultCronDateTimes.toList()
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
