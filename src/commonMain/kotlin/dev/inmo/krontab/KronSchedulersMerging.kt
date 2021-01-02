package dev.inmo.krontab

import dev.inmo.krontab.collection.CollectionKronScheduler
import dev.inmo.krontab.internal.CronDateTime
import dev.inmo.krontab.internal.CronDateTimeScheduler

/**
 * Create new one [CollectionKronScheduler] to include all [KronScheduler]s of [this] [Iterator]
 *
 * @see CollectionKronScheduler
 * @see CollectionKronScheduler.include
 */
fun Iterator<KronScheduler>.merge(): KronScheduler {
    val cronDateTimes = mutableListOf<CronDateTime>()
    val collectionScheduler = CollectionKronScheduler()
    forEach {
        when (it) {
            is CronDateTimeScheduler -> cronDateTimes.addAll(it.cronDateTimes)
            else -> collectionScheduler.include(it)
        }
    }
    if (cronDateTimes.isNotEmpty()) {
        collectionScheduler.include(CronDateTimeScheduler(cronDateTimes))
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
inline fun Iterable<KronScheduler>.merge(): KronScheduler = iterator().merge()

/**
 * @return Vararg shortcut for [merge]
 */
@Suppress("NOTHING_TO_INLINE")
inline fun merge(vararg kronDateTimeSchedulers: KronScheduler) = kronDateTimeSchedulers.iterator().merge()
