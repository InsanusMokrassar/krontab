package dev.inmo.krontab.internal

import com.soywiz.klock.DateTime
import dev.inmo.krontab.*
import dev.inmo.krontab.collection.plus

/**
 * Cron-oriented realisation of [KronScheduler]
 *
 * @see dev.inmo.krontab.AnyTimeScheduler
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
    internal val cronDateTimes: List<CronDateTime>
) : KronScheduler {
    /**
     * @return Near date using [cronDateTimes] list and getting the [Iterable.minByOrNull] one
     *
     * @see toNearDateTime
     */
    override suspend fun next(relatively: DateTime): DateTime {
        return cronDateTimes.mapNotNull { it.toNearDateTime(relatively) }.minOrNull() ?: getAnyNext(relatively)
    }
}

internal fun mergeCronDateTimeSchedulers(schedulers: List<CronDateTimeScheduler>) = CronDateTimeScheduler(
    schedulers.flatMap { it.cronDateTimes }
)

/**
 * @return New instance of [CronDateTimeScheduler] with all unique [CronDateTimeScheduler.cronDateTimes] of
 * [kronSchedulers] included
 */
@Deprecated("Will be removed in next major release", ReplaceWith("merge", "dev.inmo.krontab"))
fun merge(kronSchedulers: List<KronScheduler>) = kronSchedulers.apply { dev.inmo.krontab.merge() }

/**
 * @return Vararg shortcut for [dev.inmo.krontab.merge]
 */
@Suppress("NOTHING_TO_INLINE")
@Deprecated("Will be removed in next major release", ReplaceWith("merge", "dev.inmo.krontab"))
inline fun merge(vararg kronDateTimeSchedulers: KronScheduler) = kronDateTimeSchedulers.apply { dev.inmo.krontab.merge() }
/**
 * @return Vararg shortcut for [dev.inmo.krontab.merge]
 */
@Suppress("NOTHING_TO_INLINE")
@Deprecated("Will be removed in next major release", ReplaceWith("merge", "dev.inmo.krontab"))
inline fun KronScheduler.plus(other: KronScheduler) = this + other
