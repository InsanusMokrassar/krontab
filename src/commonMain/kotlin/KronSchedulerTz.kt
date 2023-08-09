package dev.inmo.krontab

import korlibs.time.DateTime
import korlibs.time.DateTimeTz

/**
 * This interface extending [KronScheduler] to use [DateTimeTz] with taking into account offset of incoming time for
 * [next] operation.
 *
 * @see dev.inmo.krontab.internal.CronDateTimeScheduler
 * @see dev.inmo.krontab.KronScheduler
 */
interface KronSchedulerTz : KronScheduler {
    suspend fun next(relatively: DateTimeTz): DateTimeTz?

    override suspend fun next(relatively: DateTime): DateTime? = next(relatively.local) ?.local
}

suspend fun KronSchedulerTz.nextOrRelative(relatively: DateTimeTz): DateTimeTz = next(relatively) ?: getAnyNext(
    relatively.local
).toOffsetUnadjusted(relatively.offset)
suspend fun KronSchedulerTz.nextOrNowWithOffset(): DateTimeTz = DateTimeTz.nowLocal().let {
    next(it) ?: getAnyNext(
        it.local
    ).toOffsetUnadjusted(it.offset)
}

suspend fun KronScheduler.next(relatively: DateTimeTz) = if (this is KronSchedulerTz) {
    this.next(relatively)
} else {
    this.next(relatively.local) ?.toOffsetUnadjusted(relatively.offset)
}

suspend fun KronScheduler.nextTimeZoned() = next(DateTime.now().local)
