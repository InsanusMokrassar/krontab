package com.insanusmokrassar.krontab

import com.insanusmokrassar.krontab.builder.buildSchedule
import com.insanusmokrassar.krontab.internal.CronDateTime

internal val anyCronDateTime by lazy {
    CronDateTime()
}
val AnyTimeScheduler: KronScheduler by lazy {
    CronDateTimeScheduler(listOf(anyCronDateTime))
}
val EverySecondScheduler: KronScheduler
    get() = AnyTimeScheduler

val EveryMinuteScheduler: KronScheduler by lazy {
    buildSchedule { minutes { 0 every 1 } }
}

val EveryHourScheduler: KronScheduler by lazy {
    buildSchedule { hours { 0 every 1 } }
}

val EveryDayOfMonthScheduler: KronScheduler by lazy {
    buildSchedule { dayOfMonth { 0 every 1 } }
}

val EveryMonthScheduler: KronScheduler by lazy {
    buildSchedule { months { 0 every 1 } }
}