package com.github.insanusmokrassar.krontab

suspend fun doWhile(scheduleConfig: String, block: suspend () -> Boolean) {
    val scheduler = CronDateTimeScheduler(parse(scheduleConfig))

    scheduler.doInLoop(block)
}

suspend fun executeOnce(scheduleConfig: String, block: suspend () -> Boolean) {
    val scheduler = CronDateTimeScheduler(parse(scheduleConfig))

    scheduler.doInLoop {
        block()
        false
    }
}
