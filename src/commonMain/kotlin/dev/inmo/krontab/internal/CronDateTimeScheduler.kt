package dev.inmo.krontab.internal

import com.soywiz.klock.DateTime
import dev.inmo.krontab.KronScheduler

/**
 * Cron-oriented realisation of [KronScheduler]
 *
 * @see dev.inmo.krontab.AnyTimeScheduler
 * @see dev.inmo.krontab.EveryMillisecondScheduler
 * @see dev.inmo.krontab.EverySecondScheduler
 * @see dev.inmo.krontab.EveryMinuteScheduler
 * @see dev.inmo.krontab.EveryHourScheduler
 * @see dev.inmo.krontab.EveryDayOfMonthScheduler
 * @see dev.inmo.krontab.EveryMonthScheduler
 * @see dev.inmo.krontab.EveryYearScheduler
 *
 * @see dev.inmo.krontab.builder.buildSchedule
 * @see dev.inmo.krontab.builder.SchedulerBuilder
 */
internal data class CronDateTimeScheduler internal constructor(
    internal val cronDateTime: CronDateTime
) : KronScheduler {
    /**
     * @return Near date using [cronDateTimes] list and getting the [Iterable.minByOrNull] one
     *
     * @see toNearDateTime
     */
    override suspend fun next(relatively: DateTime): DateTime? {
        return cronDateTime.toNearDateTime(relatively)
    }
}

internal fun mergeCronDateTimeSchedulers(
    schedulers: List<CronDateTimeScheduler>
): CronDateTimeScheduler = CronDateTimeScheduler(
    schedulers.map { it.cronDateTime }.merge()
)
