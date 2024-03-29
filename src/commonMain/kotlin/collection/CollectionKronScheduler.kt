package dev.inmo.krontab.collection

import korlibs.time.DateTime
import korlibs.time.DateTimeTz
import dev.inmo.krontab.*
import dev.inmo.krontab.internal.*

/**
 * This scheduler will be useful in case you want to unite several different [KronScheduler]s
 */
data class CollectionKronScheduler internal constructor(
    internal val schedulers: MutableList<KronScheduler>
) : KronSchedulerTz {
    internal constructor() : this(mutableListOf())

    /**
     * Add [kronScheduler] into its [schedulers] list
     *
     * * When [kronScheduler] is [CronDateTimeScheduler] it will merge all [CronDateTimeScheduler]s from [schedulers] list
     * and this [kronScheduler] using [mergeCronDateTimeSchedulers] function
     * * When [kronScheduler] is [CollectionKronScheduler] it this instance will include all [kronScheduler]
     * [schedulers]
     * * Otherwise [kronScheduler] will be added to [schedulers] list
     */
    fun include(kronScheduler: KronScheduler) {
        when (kronScheduler) {
            is CronDateTimeScheduler -> {
                val resultCronDateTimes = mutableListOf(kronScheduler)
                schedulers.removeAll {
                    if (it is CronDateTimeScheduler) {
                        resultCronDateTimes.add(it)
                    } else {
                        false
                    }
                }
                schedulers.add(
                    mergeCronDateTimeSchedulers(resultCronDateTimes)
                )
            }
            is CronDateTimeSchedulerTz -> {
                val newCronDateTimes = mutableListOf(kronScheduler.cronDateTime)
                schedulers.removeAll {
                    if (it is CronDateTimeSchedulerTz && it.offset == kronScheduler.offset) {
                        newCronDateTimes.add(it.cronDateTime)
                        true
                    } else {
                        false
                    }
                }
                schedulers.add(CronDateTimeSchedulerTz(newCronDateTimes.merge(), kronScheduler.offset))
            }
            is CollectionKronScheduler -> kronScheduler.schedulers.forEach {
                include(it)
            }
            else -> schedulers.add(kronScheduler)
        }
    }

    override suspend fun next(relatively: DateTime): DateTime {
        return schedulers.mapNotNull { it.next(relatively) }.minOrNull() ?: getAnyNext(relatively)
    }

    override suspend fun next(relatively: DateTimeTz): DateTimeTz {
        return schedulers.mapNotNull { it.next(relatively) }.minOrNull() ?: getAnyNext(relatively.local).toOffsetUnadjusted(relatively.offset)
    }
}
