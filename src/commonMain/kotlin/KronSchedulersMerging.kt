package dev.inmo.krontab

import dev.inmo.krontab.collection.CollectionKronScheduler
import dev.inmo.krontab.collection.includeAll
import dev.inmo.krontab.internal.*

/**
 * Create new one [CollectionKronScheduler] to include all [KronScheduler]s of [this] [Iterator]
 *
 * @see CollectionKronScheduler
 * @see CollectionKronScheduler.include
 */
fun Iterator<KronScheduler>.merge(): CollectionKronScheduler {
    val cronDateTimes = mutableListOf<CronDateTime>()
    val timezonedCronDateTimes = mutableListOf<CronDateTimeSchedulerTz>()
    val collectionScheduler = CollectionKronScheduler()
    forEach {
        when (it) {
            is CronDateTimeScheduler -> cronDateTimes.add(it.cronDateTime)
            is CronDateTimeSchedulerTz -> timezonedCronDateTimes.add(it)
            else -> collectionScheduler.include(it)
        }
    }
    if (cronDateTimes.isNotEmpty()) {
        collectionScheduler.include(CronDateTimeScheduler(cronDateTimes.merge()))
    }
    if (timezonedCronDateTimes.isNotEmpty()) {
        collectionScheduler.includeAll(mergeCronDateTimeSchedulers(timezonedCronDateTimes))
    }
    return collectionScheduler
}

/**
 * Create new one [CollectionKronScheduler] to include all [KronScheduler]s of [this] [Iterator]
 *
 * @see CollectionKronScheduler
 * @see CollectionKronScheduler.include
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Iterable<KronScheduler>.merge(): CollectionKronScheduler = iterator().merge()

/**
 * @return Vararg shortcut for [merge]
 */
@Suppress("NOTHING_TO_INLINE")
inline fun merge(vararg kronDateTimeSchedulers: KronScheduler): CollectionKronScheduler = kronDateTimeSchedulers.iterator().merge()
