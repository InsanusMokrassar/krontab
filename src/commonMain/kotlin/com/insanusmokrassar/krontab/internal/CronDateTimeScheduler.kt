package com.insanusmokrassar.krontab.internal

import com.insanusmokrassar.krontab.KronScheduler
import com.insanusmokrassar.krontab.anyCronDateTime
import com.soywiz.klock.DateTime

/**
 * Cron-oriented realisation of [KronScheduler]
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
        return cronDateTimes.map { it.toNearDateTime(relatively) }.min() ?: anyCronDateTime.toNearDateTime(relatively)
    }
}

