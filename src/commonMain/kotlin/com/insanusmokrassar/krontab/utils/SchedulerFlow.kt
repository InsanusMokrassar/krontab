package com.insanusmokrassar.krontab.utils

import com.insanusmokrassar.krontab.KronScheduler
import com.soywiz.klock.DateTime
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@FlowPreview
fun KronScheduler.asFlow(): Flow<DateTime> = SchedulerFlow(this)

@FlowPreview
class SchedulerFlow(
    private val scheduler: KronScheduler
) : AbstractFlow<DateTime>() {
    @FlowPreview
    override suspend fun collectSafely(collector: FlowCollector<DateTime>) {
        while (true) {
            val now = DateTime.now()
            val nextTime = scheduler.next(now)
            val sleepDelay = (nextTime - now).millisecondsLong
            delay(sleepDelay)
            collector.emit(nextTime)
        }
    }
}