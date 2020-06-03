package com.insanusmokrassar.krontab

import com.soywiz.klock.DateTime
import kotlinx.coroutines.delay

suspend inline fun KronScheduler.doWhile(noinline block: suspend () -> Boolean) {
    do {
        delay((next() - DateTime.now()).millisecondsLong)
    } while (block())
}
suspend inline fun doWhile(
    scheduleConfig: String,
    noinline block: suspend () -> Boolean
) = createSimpleScheduler(scheduleConfig).doWhile(block)

suspend inline fun KronScheduler.doInfinity(noinline block: suspend () -> Unit) = doWhile {
    block()
    true
}
suspend inline fun doInfinity(
    scheduleConfig: String,
    noinline block: suspend () -> Unit
) = createSimpleScheduler(scheduleConfig).doInfinity(block)

suspend inline fun KronScheduler.doOnce(noinline block: suspend () -> Unit) = doWhile {
    block()
    false
}
suspend inline fun doOnce(
    scheduleConfig: String,
    noinline block: suspend () -> Unit
) = createSimpleScheduler(scheduleConfig).doOnce(block)
