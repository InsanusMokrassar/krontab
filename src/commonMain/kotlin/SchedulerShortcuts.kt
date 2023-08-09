package dev.inmo.krontab

import korlibs.time.DateTime
import dev.inmo.krontab.builder.buildSchedule
import dev.inmo.krontab.internal.*

internal val anyCronDateTime by lazy {
    CronDateTime()
}
internal fun getAnyNext(relatively: DateTime) = anyCronDateTime.toNearDateTime(relatively)!!

/**
 * [KronScheduler.next] will always return [korlibs.time.DateTime.now]
 */
val AnyTimeScheduler: KronScheduler by lazy {
    CronDateTimeScheduler(anyCronDateTime)
}

/**
 * [KronScheduler.next] will always return [korlibs.time.DateTime.now] + one millisecond
 */
val EveryMillisecondScheduler: KronScheduler by lazy {
    buildSchedule { milliseconds { 0 every 1 } }
}

/**
 * [KronScheduler.next] will always return [korlibs.time.DateTime.now] + one second
 */
val EverySecondScheduler: KronScheduler by lazy {
    buildSchedule { seconds { 0 every 1 } }
}

/**
 * [KronScheduler.next] will always return [korlibs.time.DateTime.now] + one minute
 */
val EveryMinuteScheduler: KronScheduler by lazy {
    buildSchedule { minutes { 0 every 1 } }
}

/**
 * [KronScheduler.next] will always return [korlibs.time.DateTime.now] + one hour
 */
val EveryHourScheduler: KronScheduler by lazy {
    buildSchedule { hours { 0 every 1 } }
}

/**
 * [KronScheduler.next] will always return [korlibs.time.DateTime.now] + one day
 */
val EveryDayOfMonthScheduler: KronScheduler by lazy {
    buildSchedule { dayOfMonth { 0 every 1 } }
}

/**
 * [KronScheduler.next] will always return [korlibs.time.DateTime.now] + one month
 */
val EveryMonthScheduler: KronScheduler by lazy {
    buildSchedule { months { 0 every 1 } }
}

/**
 * [KronScheduler.next] will always return [korlibs.time.DateTime.now] + one year
 */
val EveryYearScheduler: KronScheduler by lazy {
    buildSchedule { years { 0 every 1 } }
}

/**
 * Shortcut for [EveryMillisecondScheduler]
 */
inline val KronScheduler.Companion.everyMillisecond
    get() = EveryMillisecondScheduler

/**
 * Shortcut for [EverySecondScheduler]
 */
inline val KronScheduler.Companion.everySecond
    get() = EverySecondScheduler

/**
 * Shortcut for [EveryMinuteScheduler]
 */
inline val KronScheduler.Companion.everyMinute
    get() = EveryMinuteScheduler

/**
 * Shortcut for [EveryHourScheduler]
 */
inline val KronScheduler.Companion.hourly
    get() = EveryHourScheduler

/**
 * Shortcut for [EveryDayOfMonthScheduler]
 */
inline val KronScheduler.Companion.daily
    get() = EveryDayOfMonthScheduler

/**
 * Shortcut for [EveryMonthScheduler]
 */
inline val KronScheduler.Companion.monthly
    get() = EveryMonthScheduler

/**
 * Shortcut for [EveryYearScheduler]
 */
inline val KronScheduler.Companion.annually
    get() = EveryYearScheduler
