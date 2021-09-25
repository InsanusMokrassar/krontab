package dev.inmo.krontab.utils

import com.soywiz.klock.DateTime
import com.soywiz.klock.DateTimeTz
import dev.inmo.krontab.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * This [Flow] will trigger emitting each near time which will be returned from [this] [KronScheduler] with attention to
 * time zones
 *
 * @see channelFlow
 * @see KronSchedulerTz.doWhile
 */
@FlowPreview
fun KronSchedulerTz.asTzFlow(): Flow<DateTimeTz> = channelFlow {
    doWhile {
        send(DateTime.nowLocal())
        isActive
    }
}

/**
 * This method is a map for [asTzFlow] and will works the same but return flow with [DateTime]s
 *
 * @see channelFlow
 * @see KronScheduler.doWhile
 */
@FlowPreview
fun KronScheduler.asFlow(): Flow<DateTime> = channelFlow {
    doWhile {
        send(DateTime.now())
        isActive
    }
}

/**
 * This [Flow] will trigger emitting each near time which will be returned from [this] [KronScheduler] with attention to
 * time zones
 *
 * @see channelFlow
 * @see KronScheduler.asFlow
 * @see KronSchedulerTz.asTzFlow
 */
@FlowPreview
fun KronScheduler.asTzFlow(): Flow<DateTimeTz> = when (this) {
    is KronSchedulerTz -> asTzFlow()
    else -> asFlow().map { it.local }
}

@Deprecated(
    "It is not recommended to use this class in future. This functionality will be removed soon",
    ReplaceWith("asFlow", "dev.inmo.krontab.utils.asFlow")
)
@FlowPreview
class SchedulerFlow(
    private val scheduler: KronScheduler
) : AbstractFlow<DateTime>() {
    @FlowPreview
    override suspend fun collectSafely(collector: FlowCollector<DateTime>) {
        while (true) {
            val now = DateTime.now()
            val nextTime = scheduler.next(now) ?: break
            val sleepDelay = (nextTime - now).millisecondsLong
            delay(sleepDelay)
            collector.emit(nextTime)
        }
    }
}
