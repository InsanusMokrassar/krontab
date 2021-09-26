package dev.inmo.krontab

import com.soywiz.klock.DateTime
import com.soywiz.klock.DateTimeTz
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

/**
 * Execute [block] once at the [KronScheduler.next] time and return result of [block] calculation.
 *
 * WARNING!!! If you want to launch it in parallel, you must do this explicitly.
 *
 * WARNING!!! In case if [KronScheduler.next] of [this] instance will return null, [block] will be called immediately
 */
suspend inline fun <T> KronSchedulerTz.doOnce(noinline block: suspend () -> T): T {
    nextTimeZoned() ?.let {
        delay((it - DateTimeTz.nowLocal()).millisecondsLong)
    }
    return block()
}

/**
 * Execute [block] once at the [KronScheduler.next] time and return result of [block] calculation.
 *
 * WARNING!!! If you want to launch it in parallel, you must do this explicitly.
 *
 * WARNING!!! In case if [KronScheduler.next] of [this] instance will return null, [block] will be called immediately
 */
suspend inline fun <T> KronScheduler.doOnce(noinline block: suspend () -> T): T {
    next() ?.let {
        delay((it - DateTime.now()).millisecondsLong)
    }
    return block()
}

/**
 * Will [buildSchedule] using [scheduleConfig] and call [doOnce] on it
 * @see buildSchedule
 */
suspend inline fun <T> doOnce(
    scheduleConfig: String,
    noinline block: suspend () -> T
) = with(buildSchedule(scheduleConfig)) {
    when (this) {
        is KronSchedulerTz -> doOnce(block)
        else -> doOnce(block)
    }
}

/**
 * Will execute [block] while it will return true as a result of its calculation
 */
suspend inline fun KronScheduler.doWhile(noinline block: suspend () -> Boolean) {
    do {
        delay(1L)
        val doNext = doOnce(block)
    } while (doNext)
}

/**
 * Will execute [block] while it will return true as a result of its calculation
 */
suspend inline fun KronSchedulerTz.doWhile(noinline block: suspend () -> Boolean) {
    do {
        delay(1L)
        val doNext = doOnce(block)
    } while (doNext)
}

/**
 * Will [buildSchedule] using [scheduleConfig] and call [doWhile] with [block]
 *
 * @see buildSchedule
 */
suspend inline fun doWhile(
    scheduleConfig: String,
    noinline block: suspend () -> Boolean
) = with(buildSchedule(scheduleConfig)) {
    when (this) {
        is KronSchedulerTz -> doWhile(block)
        else -> doWhile(block)
    }
}

/**
 * Will execute [block] without any checking of result
 */
suspend inline fun KronScheduler.doInfinity(noinline block: suspend () -> Unit) = doWhile {
    block()
    coroutineContext.isActive
}

/**
 * Will execute [block] without any checking of result
 */
suspend inline fun KronSchedulerTz.doInfinity(noinline block: suspend () -> Unit) = doWhile {
    block()
    coroutineContext.isActive
}
/**
 * Will [buildSchedule] using [scheduleConfig] and call [doInfinity] with [block]
 *
 * @see buildSchedule
 */
suspend inline fun doInfinity(
    scheduleConfig: String,
    noinline block: suspend () -> Unit
) = with(buildSchedule(scheduleConfig)) {
    when (this) {
        is KronSchedulerTz -> doInfinity(block)
        else -> doInfinity(block)
    }
}
