package com.github.insanusmokrassar.krontab

import com.github.insanusmokrassar.krontab.utils.*
import com.github.insanusmokrassar.krontab.utils.clamp
import com.github.insanusmokrassar.krontab.utils.minutesRange
import com.github.insanusmokrassar.krontab.utils.secondsRange

private fun parse(from: String, dataRange: IntRange): Array<Byte>? {
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

private fun parseMonths(from: String) = parse(from, monthRange)
private fun parseDaysOfMonth(from: String) = parse(from, dayOfMonthRange)
private fun parseHours(from: String) = parse(from, hoursRange)
private fun parseMinutes(from: String) = parse(from, minutesRange)
private fun parseSeconds(from: String) = parse(from, secondsRange)

private fun Array<Byte>.fillWith(
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

internal fun parse(incoming: String): List<CronDateTime> {
    val (secondsSource, minutesSource, hoursSource, dayOfMonthSource, monthSource) = incoming.split(" ")

    val secondsParsed = parseSeconds(secondsSource)
    val minutesParsed = parseSeconds(minutesSource)
    val hoursParsed = parseSeconds(hoursSource)
    val dayOfMonthParsed = parseSeconds(dayOfMonthSource)
    val monthParsed = parseSeconds(monthSource)

    val resultCronDateTimes = mutableListOf(CronDateTime())

    secondsParsed ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
        previousCronDateTime.copy(seconds = currentTime)
    }

    minutesParsed ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
        previousCronDateTime.copy(minutes = currentTime)
    }

    hoursParsed ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
        previousCronDateTime.copy(hours = currentTime)
    }

    dayOfMonthParsed ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
        previousCronDateTime.copy(dayOfMonth = currentTime)
    }

    monthParsed ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
        previousCronDateTime.copy(month = currentTime)
    }

    return resultCronDateTimes.toList()
}
