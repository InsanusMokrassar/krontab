package com.insanusmokrassar.krontab.utils

import kotlinx.coroutines.*

fun CoroutineScope.createFailJob(forTimeMillis: Long) = launch {
    delay(forTimeMillis)
    throw IllegalStateException("This job must be completed at before this exception happen")
}
