package com.github.insanusmokrassar.krontab

suspend fun executeInfinity(scheduleConfig: String, block: suspend () -> Unit) {
    val scheduler = CronDateTimeScheduler(parse(scheduleConfig))

    scheduler.doInLoop {
        block()
        true
    }
}

suspend fun executeWhile(scheduleConfig: String, block: suspend () -> Boolean) {
    val scheduler = CronDateTimeScheduler(parse(scheduleConfig))

    scheduler.doInLoop(block)
}

suspend fun executeOnce(scheduleConfig: String, block: suspend () -> Unit) {
    val scheduler = CronDateTimeScheduler(parse(scheduleConfig))

    scheduler.doInLoop {
        block()
        false
    }
}
