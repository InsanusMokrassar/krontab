package dev.inmo.krontab.utils

import com.soywiz.klock.DateTime
import com.soywiz.klock.DateTimeTz
import dev.inmo.krontab.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

/**
 * This [Flow] will trigger emitting each near time which will be returned from [this] [KronScheduler] with attention to
 * time zones
 *
 * @see channelFlow
 * @see KronSchedulerTz.doInfinityTz
 */
@FlowPreview
fun KronScheduler.asTzFlow(): Flow<DateTimeTz> = channelFlow {
    doInfinityTz {
        send(it)
    }
}

/**
 * This method is a map for [asTzFlow] and will works the same but return flow with [DateTime]s
 *
 * @see channelFlow
 * @see KronScheduler.doInfinityLocal
 */
@FlowPreview
fun KronScheduler.asFlow(): Flow<DateTime> = channelFlow {
    doInfinityLocal {
        send(it)
    }
}
