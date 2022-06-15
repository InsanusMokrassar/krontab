package dev.inmo.krontab

import com.soywiz.klock.DateTimeTz

class LambdaKronSchedulerTz(
    private val onNext: suspend (DateTimeTz) -> DateTimeTz?
) : KronSchedulerTz {
    override suspend fun next(relatively: DateTimeTz): DateTimeTz? = onNext(relatively)
}

fun KronSchedulerTz(
    onNext: suspend (DateTimeTz) -> DateTimeTz?
) = LambdaKronSchedulerTz(onNext)
