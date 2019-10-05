package com.github.insanusmokrassar.krontab

suspend inline fun executeWith(
    scheduler: CronDateTimeScheduler,
    noinline block: suspend () -> Boolean
) = scheduler.doInLoop(block)

suspend inline fun executeInfinity(
    scheduleConfig: String,
    noinline block: suspend () -> Unit
) = createCronDateTimeScheduler(scheduleConfig).doInLoop {
    block()
    true
}

suspend inline fun executeWhile(
    scheduleConfig: String,
    noinline block: suspend () -> Boolean
) = createCronDateTimeScheduler(scheduleConfig).doInLoop(block)

suspend inline fun executeOnce(
    scheduleConfig: String,
    noinline block: suspend () -> Unit
) = createCronDateTimeScheduler(scheduleConfig).doInLoop {
    block()
    false
}
