package com.insanusmokrassar.krontab

import com.soywiz.klock.DateTime
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay

/**
 * Execute [block] once at the [KronScheduler.next] time and return result of [block] calculation.
 *
 * WARNING!!! If you want to launch it in parallel, you must do this explicit.
 */
suspend inline fun <T> KronScheduler.doOnce(noinline block: suspend () -> T): T {
    delay((next() - DateTime.now()).millisecondsLong)
    return block()
}

/**
 * Will [createSimpleScheduler] using [scheduleConfig] and call [doOnce] on it
 */
suspend inline fun <T> doOnce(
    scheduleConfig: String,
    noinline block: suspend () -> T
) = createSimpleScheduler(scheduleConfig).doOnce(block)

/**
 * Will execute [block] while it will return true as a result of its calculation
 */
suspend inline fun KronScheduler.doWhile(noinline block: suspend () -> Boolean) {
    do { val doNext = doOnce(block) } while (doNext)
}

/**
 * Will [createSimpleScheduler] using [scheduleConfig] and call [doWhile] with [block]
 */
suspend inline fun doWhile(
    scheduleConfig: String,
    noinline block: suspend () -> Boolean
) = createSimpleScheduler(scheduleConfig).doWhile(block)

/**
 * Will execute [block] without any checking of result
 */
suspend inline fun KronScheduler.doInfinity(noinline block: suspend () -> Unit) = doWhile {
    block()
    true
}
/**
 * Will [createSimpleScheduler] using [scheduleConfig] and call [doInfinity] with [block]
 */
suspend inline fun doInfinity(
    scheduleConfig: String,
    noinline block: suspend () -> Unit
) = createSimpleScheduler(scheduleConfig).doInfinity(block)
