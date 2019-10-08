package com.insanusmokrassar.krontab

import com.insanusmokrassar.krontab.internal.CronDateTime
import com.insanusmokrassar.krontab.builder.buildSchedule

internal val anyCronDateTime by lazy {
    CronDateTime()
}
val AnyTimeScheduler by lazy {
    CronDateTimeScheduler(listOf(anyCronDateTime))
}
val EverySecondScheduler
    get() = AnyTimeScheduler

val EveryMinuteScheduler by lazy {
    buildSchedule { minutes { 0 every 1 } }
}

val EveryHourScheduler by lazy {
    buildSchedule { hours { 0 every 1 } }
}

val EveryDayOfMonthScheduler by lazy {
    buildSchedule { dayOfMonth { 0 every 1 } }
}

val EveryMonthScheduler by lazy {
    buildSchedule { months { 0 every 1 } }
}