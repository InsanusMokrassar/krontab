package com.github.insanusmokrassar.krontab

suspend inline fun executeWith(
    scheduler: CronDateTimeScheduler,
    noinline block: suspend () -> Boolean
) = scheduler.doInLoop(block)

suspend fun executeInfinity(scheduleConfig: String, block: suspend () -> Unit) {
    val scheduler = createCronDateTimeScheduler(scheduleConfig)

    scheduler.doInLoop {
        block()
        true
    }
}

suspend fun executeWhile(scheduleConfig: String, block: suspend () -> Boolean) {
    val scheduler = createCronDateTimeScheduler(scheduleConfig)

    scheduler.doInLoop(block)
}

suspend fun executeOnce(scheduleConfig: String, block: suspend () -> Unit) {
    val scheduler = createCronDateTimeScheduler(scheduleConfig)

    scheduler.doInLoop {
        block()
        false
    }
}
