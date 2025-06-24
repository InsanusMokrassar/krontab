package dev.inmo.krontab

import korlibs.time.DateTime
import korlibs.time.DateTimeTz
import korlibs.time.millisecondsLong
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
suspend inline fun <T> KronScheduler.doOnce(block: (DateTime) -> T): T {
    val time = nextOrNow().also {
        delay((it - DateTime.now()).millisecondsLong)
    }
    return block(time)
}

/**
 * Execute [block] once at the [KronScheduler.next] time and return result of [block] calculation.
 *
 * WARNING!!! If you want to launch it in parallel, you must do this explicitly.
 *
 * WARNING!!! In case if [KronScheduler.next] of [this] instance will return null, [block] will be called immediately
 */
@Deprecated("Replaceable", ReplaceWith("doOnce", "dev.inmo.krontab.doOnce"))
suspend inline fun <T> KronScheduler.doOnceLocal(block: (DateTime) -> T): T = doOnce(block)

/**
 * Execute [block] once at the [KronScheduler.next] time and return result of [block] calculation.
 *
 * WARNING!!! This method will use local time instead of utc
 *
 * WARNING!!! If you want to launch it in parallel, you must do this explicitly.
 *
 * WARNING!!! In case if [KronScheduler.next] of [this] instance will return null, [block] will be called immediatelly
 */
suspend inline fun <T> KronScheduler.doOnceTz(noinline block: suspend (DateTimeTz) -> T): T {
    val time = when (this) {
        is KronSchedulerTz -> nextOrNowWithOffset()
        else -> nextTimeZoned() ?: DateTimeTz.nowLocal()
    }
    delay((time - DateTimeTz.nowLocal()).millisecondsLong)
    return block(time)
}

/**
 * Will [buildSchedule] using [scheduleConfig] and call [doOnce] on it
 * @see buildSchedule
 */
suspend inline fun <T> doOnce(
    @Language("CronExp") scheduleConfig: String,
    block: (DateTime) -> T
) = buildSchedule(scheduleConfig).doOnce(block)

/**
 * Will [buildSchedule] using [scheduleConfig] and call [doOnce] on it
 * @see buildSchedule
 */
suspend inline fun <T> doOnceTz(
    @Language("CronExp") scheduleConfig: String,
    noinline block: suspend (DateTimeTz) -> T
) = buildSchedule(scheduleConfig).doOnceTz(block)


/**
 * Will execute [block] while it will return true as a result of its calculation
 */
suspend inline fun KronScheduler.doWhile(block: (DateTime) -> Boolean) {
    var latest: DateTime? = null
    do {
        delay(1L)
        val result = doOnce {
            if (latest != it) {
                latest = it
                block(it)
            } else {
                null
            }
        }
    } while (result == null || result)
}
/**
 * Will execute [block] while it will return true as a result of its calculation
 */
@Deprecated("Replaceable", ReplaceWith("doWhile", "dev.inmo.krontab.doWhile"))
suspend inline fun KronScheduler.doWhileLocal(block: (DateTime) -> Boolean) = doWhile(block)

/**
 * Will execute [block] while it will return true as a result of its calculation
 */
suspend inline fun KronScheduler.doWhileTz(noinline block: suspend (DateTimeTz) -> Boolean) {
    var latest: DateTimeTz? = null
    do {
        delay(1L)
        val result = doOnceTz {
            if (latest != it) {
                latest = it
                block(it)
            } else {
                null
            }
        }
    } while (result == null || result)
}

/**
 * Will [buildSchedule] using [scheduleConfig] and call [doWhile] with [block]
 *
 * @see buildSchedule
 */
suspend inline fun doWhile(
    @Language("CronExp") scheduleConfig: String,
    block: (DateTime) -> Boolean
) = buildSchedule(scheduleConfig).doWhile(block)

/**
 * Will [buildSchedule] using [scheduleConfig] and call [doWhile] with [block]
 *
 * @see buildSchedule
 */
@Deprecated("Replaceable", ReplaceWith("doWhile", "dev.inmo.krontab.doWhile"))
suspend inline fun doWhileLocal(
    @Language("CronExp") scheduleConfig: String,
    block: (DateTime) -> Boolean
) = doWhile(scheduleConfig, block)

/**
 * Will [buildSchedule] using [scheduleConfig] and call [doWhile] with [block]
 *
 * @see buildSchedule
 */
suspend inline fun doWhileTz(
    @Language("CronExp") scheduleConfig: String,
    noinline block: suspend (DateTimeTz) -> Boolean
) = buildSchedule(scheduleConfig).doWhileTz(block)


/**
 * Will execute [block] without any checking of result
 */
suspend inline fun KronScheduler.doInfinity(block: (DateTime) -> Unit) = doWhile {
    block(it)
    coroutineContext.isActive
}
/**
 * Will execute [block] without any checking of result
 */
@Deprecated("Replaceable", ReplaceWith("doInfinity", "dev.inmo.krontab.doInfinity"))
suspend inline fun KronScheduler.doInfinityLocal(block: (DateTime) -> Unit) = doInfinity(block)

/**
 * Will execute [block] without any checking of result
 */
suspend inline fun KronScheduler.doInfinityTz(noinline block: suspend (DateTimeTz) -> Unit) = doWhileTz {
    block(it)
    coroutineContext.isActive
}

/**
 * Will [buildSchedule] using [scheduleConfig] and call [doInfinity] with [block]
 *
 * @see buildSchedule
 */
suspend inline fun doInfinity(
    @Language("CronExp") scheduleConfig: String,
    block: (DateTime) -> Unit
) = buildSchedule(scheduleConfig).doInfinity(block)

/**
 * Will [buildSchedule] using [scheduleConfig] and call [doInfinity] with [block]
 *
 * @see buildSchedule
 */
@Deprecated("Replaceable", ReplaceWith("doInfinity", "dev.inmo.krontab.doInfinity"))
suspend inline fun doInfinityLocal(
    @Language("CronExp") scheduleConfig: String,
    block: (DateTime) -> Unit
) = doInfinity(scheduleConfig, block)

/**
 * Will [buildSchedule] using [scheduleConfig] and call [doInfinity] with [block]
 *
 * @see buildSchedule
 */
suspend inline fun doInfinityTz(
    @Language("CronExp") scheduleConfig: String,
    noinline block: suspend (DateTimeTz) -> Unit
) = buildSchedule(scheduleConfig).doInfinityTz(block)
