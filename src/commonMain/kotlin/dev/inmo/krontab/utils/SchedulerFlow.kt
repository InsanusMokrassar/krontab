package dev.inmo.krontab.utils

import korlibs.time.DateTime
import korlibs.time.DateTimeTz
import korlibs.time.milliseconds
import dev.inmo.krontab.KronScheduler
import dev.inmo.krontab.next
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive

/**
 * **This flow is [cold](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/)**
 *
 * Will emit all the [KronScheduler.next] as soon as possible. In case [KronScheduler.next] return null, flow will
 * be completed
 *
 * @param since Will be used as the first parameter for [KronScheduler.next] fun. If passed null, `flow`
 * will always start since the moment of collecting start
 */
fun KronScheduler.asTzFlowWithoutDelays(since: DateTimeTz? = null): Flow<DateTimeTz> = flow {
    var previous = since ?: DateTime.nowLocal()
    while (currentCoroutineContext().isActive) {
        val next = next(previous) ?: break
        emit(next)
        previous = next + 1.milliseconds
    }
}

/**
 * **This flow is [cold](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/)**
 *
 * This [Flow] will use [asTzFlowWithoutDelays], but stop on each time until this time will happen
 */
fun KronScheduler.asTzFlowWithDelays(): Flow<DateTimeTz> = asTzFlowWithoutDelays().onEach { futureHappenTime ->
    val now = DateTime.nowLocal()

    delay((futureHappenTime - now).millisecondsLong)
}

/**
 * **This flow is [cold](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/)**
 *
 * This [Flow] will use [asTzFlowWithoutDelays], but stop on each time until this time will happen
 */
@Deprecated(
    "Behaviour will be changed. In some of near versions this flow will not delay executions",
    ReplaceWith("this.asTzFlowWithDelays()", "dev.inmo.krontab.utils.asTzFlowWithDelays")
)
fun KronScheduler.asTzFlow(): Flow<DateTimeTz> = asTzFlowWithDelays()

/**
 * **This flow is [cold](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/)**
 *
 * Will emit all the [KronScheduler.next] as soon as possible. In case [KronScheduler.next] return null, flow will
 * be completed
 *
 * @param since Will be used as the first parameter for [KronScheduler.next] fun. If passed null, `flow`
 * will always start since the moment of collecting start
 */
fun KronScheduler.asFlowWithoutDelays(since: DateTime? = null): Flow<DateTime> = flow {
    var previous = since ?: DateTime.now()
    while (currentCoroutineContext().isActive) {
        val next = next(previous) ?: break
        emit(next)
        previous = next + 1.milliseconds
    }
}

/**
 * **This flow is [cold](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/)**
 *
 * This [Flow] will use [asFlowWithoutDelays], but stop on each time until this time will happen
 */
fun KronScheduler.asFlowWithDelays(): Flow<DateTime> = asFlowWithoutDelays().onEach { futureHappenTime ->
    val now = DateTime.now()

    delay((futureHappenTime - now).millisecondsLong)
}

/**
 * **This flow is [cold](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/)**
 *
 * This [Flow] will use [asFlowWithDelays], but stop on each time until this time will happen
 */
@Deprecated(
    "Behaviour will be changed. In some of near versions this flow will not delay executions",
    ReplaceWith("this.asFlowWithDelays()", "dev.inmo.krontab.utils.asFlowWithDelays")
)
fun KronScheduler.asFlow(): Flow<DateTime> = asFlowWithDelays()
