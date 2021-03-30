package dev.inmo.krontab

import com.soywiz.klock.DateTime
import com.soywiz.klock.DateTimeTz
import dev.inmo.krontab.builder.buildSchedule
import dev.inmo.krontab.internal.*
import dev.inmo.krontab.internal.CronDateTime
import dev.inmo.krontab.internal.CronDateTimeScheduler

internal val anyCronDateTime by lazy {
    CronDateTime()
}
internal fun getAnyNext(relatively: DateTime) = anyCronDateTime.toNearDateTime(relatively)!!
internal fun getAnyNext(relatively: DateTimeTz) = anyCronDateTime.toNearDateTime(relatively)!!

/**
 * [KronScheduler.next] will always return [com.soywiz.klock.DateTime.now]
 */
val AnyTimeScheduler: KronScheduler by lazy {
    CronDateTimeScheduler(listOf(anyCronDateTime))
}

/**
 * [KronScheduler.next] will always return [com.soywiz.klock.DateTime.now] + one second
 */
val EverySecondScheduler: KronScheduler by lazy {
    buildSchedule { seconds { 0 every 1 } }
}

/**
 * [KronScheduler.next] will always return [com.soywiz.klock.DateTime.now] + one minute
 */
val EveryMinuteScheduler: KronScheduler by lazy {
    buildSchedule { minutes { 0 every 1 } }
}

/**
 * [KronScheduler.next] will always return [com.soywiz.klock.DateTime.now] + one hour
 */
val EveryHourScheduler: KronScheduler by lazy {
    buildSchedule { hours { 0 every 1 } }
}

/**
 * [KronScheduler.next] will always return [com.soywiz.klock.DateTime.now] + one day
 */
val EveryDayOfMonthScheduler: KronScheduler by lazy {
    buildSchedule { dayOfMonth { 0 every 1 } }
}

/**
 * [KronScheduler.next] will always return [com.soywiz.klock.DateTime.now] + one month
 */
val EveryMonthScheduler: KronScheduler by lazy {
    buildSchedule { months { 0 every 1 } }
}

/**
 * [KronScheduler.next] will always return [com.soywiz.klock.DateTime.now] + one year
 */
val EveryYearScheduler: KronScheduler by lazy {
    buildSchedule { years { 0 every 1 } }
}