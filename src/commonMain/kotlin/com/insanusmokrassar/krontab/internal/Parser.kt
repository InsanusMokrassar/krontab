package com.insanusmokrassar.krontab.internal

import com.insanusmokrassar.krontab.utils.clamp

private fun createSimpleScheduler(from: String, dataRange: IntRange): Array<Byte>? {
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

internal fun parseMonths(from: String) = createSimpleScheduler(from, monthRange)
internal fun parseDaysOfMonth(from: String) = createSimpleScheduler(from, dayOfMonthRange)
internal fun parseHours(from: String) = createSimpleScheduler(from, hoursRange)
internal fun parseMinutes(from: String) = createSimpleScheduler(from, minutesRange)
internal fun parseSeconds(from: String) = createSimpleScheduler(from, secondsRange)

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

