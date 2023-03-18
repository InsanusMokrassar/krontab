package dev.inmo.krontab.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun CoroutineScope.createFailJob(forTimeMillis: Long) = launch {
    delay(forTimeMillis)
    throw IllegalStateException("This job must be completed at before this exception happen")
}
