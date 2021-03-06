package dev.inmo.krontab.utils

import kotlinx.coroutines.*

actual fun runTest(block: suspend (scope : CoroutineScope) -> Unit): dynamic = GlobalScope.promise { block(this) }
