package com.insanusmokrassar.krontab

import com.insanusmokrassar.krontab.builder.buildSchedule
import com.insanusmokrassar.krontab.internal.CronDateTime

internal val anyCronDateTime by lazy {
    CronDateTime()
}

/**
 * [KronScheduler.next] will always return [com.soywiz.klock.DateTime.now]
 */
val AnyTimeScheduler: KronScheduler by lazy {
    CronDateTimeScheduler(listOf(anyCronDateTime))
}

/**
 * [KronScheduler.next] will always return [com.soywiz.klock.DateTime.now] + one second
 */
val EverySecondScheduler: KronScheduler
    get() = AnyTimeScheduler

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