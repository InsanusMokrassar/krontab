package dev.inmo.krontab.utils

import com.soywiz.klock.DateTime
import com.soywiz.klock.DateTimeTz
import dev.inmo.krontab.KronScheduler
import dev.inmo.krontab.next
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * This [Flow] will trigger emitting each near time which will be returned from [this] [KronScheduler] with attention to
 * time zones
 *
 * @see channelFlow
 */
@FlowPreview
fun KronScheduler.asTzFlow(): Flow<DateTimeTz> = channelFlow {
    var previousTime = DateTime.nowLocal()
    while (isActive) {
        val now = DateTime.nowLocal()
        val nextTime = next(now) ?: break
        if (previousTime == nextTime) {
            delay(1L) // skip 1ms
            continue
        } else {
            previousTime = nextTime
        }
        val sleepDelay = (nextTime - DateTime.now().local).millisecondsLong
        delay(sleepDelay)
        send(nextTime)
    }
}

/**
 * This method is a map for [asTzFlow] and will works the same but return flow with [DateTime]s
 */
@FlowPreview
fun KronScheduler.asFlow(): Flow<DateTime> = asTzFlow().map { it.local }

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
