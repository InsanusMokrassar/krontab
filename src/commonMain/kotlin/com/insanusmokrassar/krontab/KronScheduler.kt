package com.insanusmokrassar.krontab

import com.soywiz.klock.DateTime

/**
 * This interface was created for abstraction of [next] operation. Currently, there is only
 * [com.insanusmokrassar.krontab.internal.CronDateTimeScheduler] realisation of this interface inside of this library,
 * but you it is possible to create your own realisation of this interface for scheduling, for example, depending of
 * users activity or something like this
 *
 * @see com.insanusmokrassar.krontab.internal.CronDateTimeScheduler
 */
interface KronScheduler {

    /**
     * @return Next [DateTime] when some action must be triggered according to settings of this instance
     *
     * @see com.insanusmokrassar.krontab.internal.CronDateTimeScheduler.next
     */
    suspend fun next(relatively: DateTime = DateTime.now()): DateTime
}
