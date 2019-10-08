package com.insanusmokrassar.krontab.internal

import com.github.insanusmokrassar.krontab.dayOfMonthRange
import com.github.insanusmokrassar.krontab.hoursRange
import com.github.insanusmokrassar.krontab.minutesRange
import com.github.insanusmokrassar.krontab.monthRange
import com.github.insanusmokrassar.krontab.secondsRange
import com.insanusmokrassar.krontab.utils.clamp

private fun createCronDateTimeScheduler(from: String, dataRange: IntRange): Array<Byte>? {
    val things = from.split(",")

    val results = things.flatMap {
        when {
            it.contains("/") -> {
                val (start, step) = it.split("/")
                val startNum = (if (start.isEmpty() || start == "*") {
                    0
                } else {
                    start.toInt()
                }).clamp(dataRange)
                val stepNum = step.toInt().clamp(dataRange)
                (startNum .. dataRange.last step stepNum).map { it }
            }
            it == "*" -> return null
            else -> listOf(it.toInt().clamp(dataRange))
        }
    }

    return results.map { it.toByte() }.toTypedArray()
}

internal fun parseMonths(from: String) = createCronDateTimeScheduler(from, monthRange)
internal fun parseDaysOfMonth(from: String) = createCronDateTimeScheduler(from, dayOfMonthRange)
internal fun parseHours(from: String) = createCronDateTimeScheduler(from, hoursRange)
internal fun parseMinutes(from: String) = createCronDateTimeScheduler(from, minutesRange)
internal fun parseSeconds(from: String) = createCronDateTimeScheduler(from, secondsRange)

internal fun Array<Byte>.fillWith(
    whereToPut: MutableList<CronDateTime>,
    createFactory: (CronDateTime, Byte) -> CronDateTime
) {
    val previousValues = whereToPut.toList()

    whereToPut.clear()

    previousValues.forEach { previousValue ->
        forEach {
            whereToPut.add(createFactory(previousValue, it))
        }
    }
}

