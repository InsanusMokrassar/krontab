package com.insanusmokrassar.krontab

import com.insanusmokrassar.krontab.internal.*
import com.soywiz.klock.DateTime

internal data class CronDateTimeScheduler internal constructor(
    internal val cronDateTimes: List<CronDateTime>
) : KronScheduler {
    override suspend fun next(relatively: DateTime): DateTime {
        return cronDateTimes.map { it.toNearDateTime(relatively) }.min() ?: anyCronDateTime.toNearDateTime(relatively)
    }
}

