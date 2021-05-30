package dev.inmo.krontab.internal

import com.soywiz.klock.DateTimeTz
import com.soywiz.klock.TimezoneOffset
import dev.inmo.krontab.KronScheduler
import dev.inmo.krontab.KronSchedulerTz

/**
 * Cron-oriented realisation of [KronScheduler] with taking into account [offset] for list of [cronDateTimes]
 *
 * @see CronDateTime
 */
internal data class CronDateTimeSchedulerTz internal constructor(
    internal val cronDateTime: CronDateTime,
    internal val offset: TimezoneOffset
) : KronSchedulerTz {
    override suspend fun next(relatively: DateTimeTz): DateTimeTz? {
        val dateTimeWithActualOffset = relatively.toOffset(offset).local
        return cronDateTime.toNearDateTime(dateTimeWithActualOffset) ?.toOffsetUnadjusted(offset) ?.toOffset(relatively.offset)
    }
}

internal fun mergeCronDateTimeSchedulers(
    schedulers: List<CronDateTimeSchedulerTz>
) = schedulers.groupBy {
    it.offset
}.map { (offset, schedulers) ->
    CronDateTimeSchedulerTz(
        schedulers.map { it.cronDateTime }.merge(),
        offset
    )
}
