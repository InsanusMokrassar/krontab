package dev.inmo.krontab.collection

import dev.inmo.krontab.KronScheduler

@Suppress("NOTHING_TO_INLINE")
inline fun CollectionKronScheduler.includeAll(kronSchedulers: List<KronScheduler>) {
    kronSchedulers.forEach {
        include(it)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun CollectionKronScheduler.includeAll(vararg kronSchedulers: KronScheduler) {
    includeAll(kronSchedulers.toList())
}

operator fun KronScheduler.plus(kronScheduler: KronScheduler): CollectionKronScheduler {
    return CollectionKronScheduler().apply {
        includeAll(this, kronScheduler)
    }
}

operator fun CollectionKronScheduler.plusAssign(kronScheduler: KronScheduler) {
    include(kronScheduler)
}
