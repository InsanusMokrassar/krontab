package com.insanusmokrassar.krontab

suspend inline fun CronDateTimeScheduler.executeInfinity(noinline block: suspend () -> Unit) = doInLoop {
    block()
    true
}
suspend inline fun executeInfinity(
    scheduleConfig: String,
    noinline block: suspend () -> Unit
) = createCronDateTimeScheduler(scheduleConfig).executeInfinity(block)

suspend inline fun CronDateTimeScheduler.executeWhile(noinline block: suspend () -> Boolean) = doInLoop(block)
suspend inline fun executeWhile(
    scheduleConfig: String,
    noinline block: suspend () -> Boolean
) = createCronDateTimeScheduler(scheduleConfig).executeWhile(block)

suspend inline fun CronDateTimeScheduler.executeOnce(noinline block: suspend () -> Unit) = doInLoop {
    block()
    false
}
suspend inline fun executeOnce(
    scheduleConfig: String,
    noinline block: suspend () -> Unit
) = createCronDateTimeScheduler(scheduleConfig).executeOnce(block)
