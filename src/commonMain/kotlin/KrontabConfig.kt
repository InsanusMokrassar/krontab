package dev.inmo.krontab

import dev.inmo.krontab.internal.*
import dev.inmo.krontab.internal.CronDateTimeScheduler
import dev.inmo.krontab.internal.CronDateTimeSchedulerTz
import dev.inmo.krontab.internal.createKronScheduler
import dev.inmo.krontab.internal.createKronSchedulerWithOffset
import dev.inmo.krontab.internal.millisecondsArrayDefault
import dev.inmo.krontab.internal.parseDaysOfMonth
import dev.inmo.krontab.internal.parseHours
import dev.inmo.krontab.internal.parseMilliseconds
import dev.inmo.krontab.internal.parseMinutes
import dev.inmo.krontab.internal.parseMonths
import dev.inmo.krontab.internal.parseOffset
import dev.inmo.krontab.internal.parseSeconds
import dev.inmo.krontab.internal.parseWeekDay
import dev.inmo.krontab.internal.parseYears
import dev.inmo.krontab.utils.Minutes
import korlibs.time.TimezoneOffset
import korlibs.time.minutes
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * This value class contains [KrontabTemplate]
 *
 * * **seconds**
 * * **minutes**
 * * **hours**
 * * **dayOfMonth**
 * * **month**
 * * **year** (optional)
 * * **offset** (optional) (can be placed anywhere after month) (must be marked with `o` at the end, for example: 60o == +01:00)
 * * **dayOfWeek** (optional) (can be placed anywhere after month)
 * * **milliseconds** (optional) (can be placed anywhere after month) (must be marked with `ms` at the end, for example: 500ms; 100-200ms)
 *
 * And each one (except of offsets) have next format:
 *
 * `{number}[,{number},...]` or `*`
 *
 * and {number} here is one of
 *
 * * {int}-{int}
 * * {int}/{int}
 * * *&#47;{int}
 * * {int}
 * * F
 * * L
 *
 * Week days must be marked with `w` at the end, and starts with 0 which means Sunday. For example, 0w == Sunday. With
 * weeks you can use syntax like with any number like seconds, for example: 0-2w means Sunday-Tuesday
 *
 * Additional info about ranges can be found in follow accordance:
 *
 * * Seconds ranges can be found in [secondsRange]
 * * Minutes ranges can be found in [minutesRange]
 * * Hours ranges can be found in [hoursRange]
 * * Days of month ranges can be found in [dayOfMonthRange]
 * * Months ranges can be found in [monthRange]
 * * Years ranges can be found in [yearRange] (in fact - any [Int])
 * * WeekDay (timezone) ranges can be found in [dayOfWeekRange]
 * * Milliseconds ranges can be found in [millisecondsRange]
 *
 * Examples:
 *
 * * "0/5 * * * *" for every five seconds triggering
 * * "0/5,L * * * *" for every five seconds triggering and on 59 second
 * * "0/15 30 * * *" for every 15th seconds in a half of each hour
 * * "0/15 30 * * * 500ms" for every 15th seconds in a half of each hour when milliseconds equal to 500
 * * "1 2 3 F,4,L 5" for triggering in near first second of second minute of third hour of first, fifth and last days of may
 * * "1 2 3 F,4,L 5 60o" for triggering in near first second of second minute of third hour of first, fifth and last days of may with timezone UTC+01:00
 * * "1 2 3 F,4,L 5 60o 0-2w" for triggering in near first second of second minute of third hour of first, fifth and last days of may in case if it will be in Sunday-Tuesday week days with timezone UTC+01:00
 * * "1 2 3 F,4,L 5 2021" for triggering in near first second of second minute of third hour of first, fifth and last days of may of 2021st year
 * * "1 2 3 F,4,L 5 2021 60o" for triggering in near first second of second minute of third hour of first, fifth and last days of may of 2021st year with timezone UTC+01:00
 * * "1 2 3 F,4,L 5 2021 60o 0-2w" for triggering in near first second of second minute of third hour of first, fifth and last days of may of 2021st year if it will be in Sunday-Tuesday week days with timezone UTC+01:00
 * * "1 2 3 F,4,L 5 2021 60o 0-2w 500ms" for triggering in near first second of second minute of third hour of first, fifth and last days of may of 2021st year if it will be in Sunday-Tuesday week days with timezone UTC+01:00 when milliseconds will be equal to 500
 *
 * @see dev.inmo.krontab.internal.createKronScheduler
 */
@Serializable
@JvmInline
value class KrontabConfig(
    @Suppress("MemberVisibilityCanBePrivate")
    val template: KrontabTemplate
) {
    /**
     * Creates __new__ [KronScheduler] based on a [template]
     *
     * @return In case when offset parameter is absent in [template] will be used [createSimpleScheduler] method and
     * returned [CronDateTimeScheduler]. In case when offset parameter there is in [template] [KrontabTemplate] will be used
     * [createKronSchedulerWithOffset] and returned [CronDateTimeSchedulerTz]
     */
    fun scheduler(): KronScheduler {
        var offsetParsed: Int? = null
        var dayOfWeekParsed: Array<Byte>? = null
        var yearParsed: Array<Int>? = null
        var millisecondsParsed: Array<Short>? = null
        val (secondsSource, minutesSource, hoursSource, dayOfMonthSource, monthSource) = template
            .split(" ")
            .filter { it.matches(KrontabConfigPartRegex) } // filter garbage from string
            .let {
                if (it.size < 5) { // reconstruction in case of insufficient arguments; 5 is amount of required arguments out of latest also code
                    it + (it.size until 5).map { "*" }
                } else {
                    it
                }
            }
            .also {
                listOfNotNull(
                    it.getOrNull(5),
                    it.getOrNull(6),
                    it.getOrNull(7),
                    it.getOrNull(8)
                ).forEach {
                    val offsetFromString = parseOffset(it)
                    val dayOfWeekFromString = parseWeekDay(it)
                    val millisecondsFromString = parseMilliseconds(it)
                    offsetParsed = offsetParsed ?: offsetFromString
                    dayOfWeekParsed = dayOfWeekParsed ?: dayOfWeekFromString
                    millisecondsParsed = millisecondsParsed ?: millisecondsFromString
                    when {
                        dayOfWeekFromString != null || offsetFromString != null || millisecondsFromString != null -> return@forEach
                        yearParsed == null -> {
                            yearParsed = parseYears(it)
                        }
                    }
                }
            }

        val secondsParsed = parseSeconds(secondsSource)
        val minutesParsed = parseMinutes(minutesSource)
        val hoursParsed = parseHours(hoursSource)
        val dayOfMonthParsed = parseDaysOfMonth(dayOfMonthSource)
        val monthParsed = parseMonths(monthSource)

        return offsetParsed ?.let { offset ->
            createKronSchedulerWithOffset(
                secondsParsed,
                minutesParsed,
                hoursParsed,
                dayOfMonthParsed,
                monthParsed,
                yearParsed,
                dayOfWeekParsed,
                TimezoneOffset(offset.minutes),
                millisecondsParsed ?: millisecondsArrayDefault
            )
        } ?: createKronScheduler(
            secondsParsed,
            minutesParsed,
            hoursParsed,
            dayOfMonthParsed,
            monthParsed,
            yearParsed,
            dayOfWeekParsed,
            millisecondsParsed ?: millisecondsArrayDefault
        )
    }

    /**
     * Creates base [KronScheduler] using [scheduler] function. In case when returned [KronScheduler] is [KronSchedulerTz],
     * it will be returned as is. Otherwise, will be created new [CronDateTimeSchedulerTz] with [defaultOffset] as
     * offset
     */
    fun scheduler(defaultOffset: Minutes): KronSchedulerTz {
        val scheduler = scheduler()
        return if (scheduler is KronSchedulerTz) {
            scheduler
        } else {
            CronDateTimeSchedulerTz(
                (scheduler as CronDateTimeScheduler).cronDateTime,
                TimezoneOffset(defaultOffset.minutes)
            )
        }
    }
}
