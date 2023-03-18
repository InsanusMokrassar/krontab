package dev.inmo.krontab.utils

import com.soywiz.klock.DateTime
import com.soywiz.klock.DateTimeTz
import com.soywiz.klock.milliseconds
import dev.inmo.krontab.*
import kotlinx.coroutines.FlowPreview
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
 * @param since Will be used as the first parameter for [KronScheduler.next] fun
 */
@FlowPreview
fun KronScheduler.asTzFlowWithoutDelays(since: DateTimeTz = DateTime.nowLocal()): Flow<DateTimeTz> = flow {
    var previous = since
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
@FlowPreview
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
@FlowPreview
fun KronScheduler.asTzFlow(): Flow<DateTimeTz> = asTzFlowWithDelays()

/**
 * **This flow is [cold](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/)**
 *
 * Will emit all the [KronScheduler.next] as soon as possible. In case [KronScheduler.next] return null, flow will
 * be completed
 *
 * @param since Will be used as the first parameter for [KronScheduler.next] fun
 */
@FlowPreview
fun KronScheduler.asFlowWithoutDelays(since: DateTime = DateTime.now()): Flow<DateTime> = flow {
    var previous = since
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
@FlowPreview
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
@FlowPreview
fun KronScheduler.asFlow(): Flow<DateTime> = asFlowWithDelays()
