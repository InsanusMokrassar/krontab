package dev.inmo.krontab.collection

import com.soywiz.klock.DateTime
import dev.inmo.krontab.KronScheduler
import dev.inmo.krontab.anyCronDateTime
import dev.inmo.krontab.internal.CronDateTimeScheduler
import dev.inmo.krontab.internal.toNearDateTime

data class CollectionKronScheduler private constructor(
    internal val schedulers: MutableList<KronScheduler>
) : KronScheduler {
    internal constructor(schedulers: List<KronScheduler>) : this(schedulers.toMutableList())
    internal constructor() : this(mutableListOf())

    fun include(kronScheduler: KronScheduler) {
        when (kronScheduler) {
            is CronDateTimeScheduler -> {
                val resultCronDateTimes = kronScheduler.cronDateTimes.toMutableList()
                schedulers.removeAll {
                    if (it is CronDateTimeScheduler) {
                        resultCronDateTimes.addAll(it.cronDateTimes)
                        true
                    } else {
                        false
                    }
                }
                schedulers.add(
                    CronDateTimeScheduler(resultCronDateTimes.distinct())
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
