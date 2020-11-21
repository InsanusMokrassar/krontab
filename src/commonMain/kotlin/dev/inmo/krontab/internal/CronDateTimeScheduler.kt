package dev.inmo.krontab.internal

import com.soywiz.klock.DateTime
import dev.inmo.krontab.KronScheduler
import dev.inmo.krontab.anyCronDateTime

/**
 * Cron-oriented realisation of [KronScheduler]
 *
 * @see dev.inmo.krontab.AnyTimeScheduler
 * @see dev.inmo.krontab.EverySecondScheduler
 * @see dev.inmo.krontab.EveryMinuteScheduler
 * @see dev.inmo.krontab.EveryHourScheduler
 * @see dev.inmo.krontab.EveryDayOfMonthScheduler
 * @see dev.inmo.krontab.EveryMonthScheduler
 *
 * @see dev.inmo.krontab.builder.buildSchedule
 * @see dev.inmo.krontab.builder.SchedulerBuilder
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

