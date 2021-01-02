package dev.inmo.krontab.collection

import com.soywiz.klock.DateTime
import dev.inmo.krontab.KronScheduler
import dev.inmo.krontab.anyCronDateTime
import dev.inmo.krontab.internal.*
import dev.inmo.krontab.internal.CronDateTimeScheduler
import dev.inmo.krontab.internal.toNearDateTime

/**
 * This scheduler will be useful in case you want to unite several different [KronScheduler]s
 */
data class CollectionKronScheduler internal constructor(
    internal val schedulers: MutableList<KronScheduler>
) : KronScheduler {
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
            is CollectionKronScheduler -> kronScheduler.schedulers.forEach {
                include(it)
            }
            else -> schedulers.add(kronScheduler)
        }
    }

    override suspend fun next(relatively: DateTime): DateTime {
        return schedulers.minOfOrNull { it.next(relatively) } ?: anyCronDateTime.toNearDateTime(relatively)
    }
}
