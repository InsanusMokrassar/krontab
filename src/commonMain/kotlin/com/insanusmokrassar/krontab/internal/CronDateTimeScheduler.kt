package com.insanusmokrassar.krontab.internal

import com.insanusmokrassar.krontab.KronScheduler
import com.insanusmokrassar.krontab.anyCronDateTime
import com.soywiz.klock.DateTime

/**
 * Cron-oriented realisation of [KronScheduler]
 *
 * @see com.insanusmokrassar.krontab.AnyTimeScheduler
 * @see com.insanusmokrassar.krontab.EverySecondScheduler
 * @see com.insanusmokrassar.krontab.EveryMinuteScheduler
 * @see com.insanusmokrassar.krontab.EveryHourScheduler
 * @see com.insanusmokrassar.krontab.EveryDayOfMonthScheduler
 * @see com.insanusmokrassar.krontab.EveryMonthScheduler
 *
 * @see com.insanusmokrassar.krontab.builder.buildSchedule
 * @see com.insanusmokrassar.krontab.builder.SchedulerBuilder
 */
internal data class CronDateTimeScheduler internal constructor(
    internal val cronDateTimes: List<CronDateTime>
) : KronScheduler {
    /**
     * @return Near date using [cronDateTimes] list and getting the [Iterable.min] one
     *
     * @see toNearDateTime
     */
    override suspend fun next(relatively: DateTime): DateTime {
        return cronDateTimes.map { it.toNearDateTime(relatively) }.minOrNull() ?: anyCronDateTime.toNearDateTime(relatively)
    }
}

