package dev.inmo.krontab

import korlibs.time.DateTime

class LambdaKronScheduler(
    private val onNext: suspend (DateTime) -> DateTime?
) : KronScheduler {
    override suspend fun next(relatively: DateTime): DateTime? = onNext(relatively)
}

fun KronScheduler(
    onNext: suspend (DateTime) -> DateTime?
) = LambdaKronScheduler(onNext)
