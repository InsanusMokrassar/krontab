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
data class CronDateTimeScheduler internal constructor(
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

/**
 * @return New instance of [CronDateTimeScheduler] with all unique [CronDateTimeScheduler.cronDateTimes] of
 * [kronDateTimeSchedulers] included
 */
@Suppress("NOTHING_TO_INLINE")
fun merge(kronDateTimeSchedulers: List<CronDateTimeScheduler>) = CronDateTimeScheduler(
    kronDateTimeSchedulers.flatMap { it.cronDateTimes }.distinct()
)

/**
 * @return Vararg shortcyut for [merge]
 */
@Suppress("NOTHING_TO_INLINE")
inline fun merge(vararg kronDateTimeSchedulers: CronDateTimeScheduler) = merge(kronDateTimeSchedulers.toList())
/**
 * Use [merge] operation to internalcreate new [CronDateTimeScheduler] with all [CronDateTimeScheduler.cronDateTimes]
 */
@Suppress("NOTHING_TO_INLINE")
inline fun CronDateTimeScheduler.plus(other: CronDateTimeScheduler) = merge(this, other)
