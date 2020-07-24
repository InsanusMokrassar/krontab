package com.insanusmokrassar.krontab

import com.soywiz.klock.DateTime
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay

/**
 * Execute [block] once at the [KronScheduler.next] time and return result of [block] calculation.
 *
 * WARNING!!! If you want to launch it in parallel, you must do this explicitly.
 */
suspend inline fun <T> KronScheduler.doOnce(noinline block: suspend () -> T): T {
    delay((next() - DateTime.now()).millisecondsLong)
    return block()
}

/**
 * Will [buildSchedule] using [scheduleConfig] and call [doOnce] on it
 * @see buildSchedule
 */
suspend inline fun <T> doOnce(
    scheduleConfig: String,
    noinline block: suspend () -> T
) = buildSchedule(scheduleConfig).doOnce(block)

/**
 * Will execute [block] while it will return true as a result of its calculation
 */
suspend inline fun KronScheduler.doWhile(noinline block: suspend () -> Boolean) {
    do { val doNext = doOnce(block) } while (doNext)
}

/**
 * Will [buildSchedule] using [scheduleConfig] and call [doWhile] with [block]
 *
 * @see buildSchedule
 */
suspend inline fun doWhile(
    scheduleConfig: String,
    noinline block: suspend () -> Boolean
) = buildSchedule(scheduleConfig).doWhile(block)

/**
 * Will execute [block] without any checking of result
 */
suspend inline fun KronScheduler.doInfinity(noinline block: suspend () -> Unit) = doWhile {
    block()
    true
}
/**
 * Will [buildSchedule] using [scheduleConfig] and call [doInfinity] with [block]
 *
 * @see buildSchedule
 */
suspend inline fun doInfinity(
    scheduleConfig: String,
    noinline block: suspend () -> Unit
) = buildSchedule(scheduleConfig).doInfinity(block)
