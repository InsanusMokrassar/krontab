package dev.inmo.krontab.internal

import com.soywiz.klock.DateTime
import dev.inmo.krontab.KronScheduler
import dev.inmo.krontab.anyCronDateTime
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

internal fun mergeCronDateTimeSchedulers(schedulers: List<CronDateTimeScheduler>) = CronDateTimeScheduler(
    schedulers.flatMap { it.cronDateTimes }
)

/**
 * @return New instance of [CronDateTimeScheduler] with all unique [CronDateTimeScheduler.cronDateTimes] of
 * [kronDateTimeSchedulers] included
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
