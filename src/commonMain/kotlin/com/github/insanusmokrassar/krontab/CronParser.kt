package com.github.insanusmokrassar.krontab

import com.github.insanusmokrassar.krontab.utils.*
import com.github.insanusmokrassar.krontab.utils.clamp
import com.github.insanusmokrassar.krontab.utils.minutesRange
import com.github.insanusmokrassar.krontab.utils.secondsRange

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

private fun parseMonths(from: String) = createCronDateTimeScheduler(from, monthRange)
private fun parseDaysOfMonth(from: String) = createCronDateTimeScheduler(from, dayOfMonthRange)
private fun parseHours(from: String) = createCronDateTimeScheduler(from, hoursRange)
private fun parseMinutes(from: String) = createCronDateTimeScheduler(from, minutesRange)
private fun parseSeconds(from: String) = createCronDateTimeScheduler(from, secondsRange)

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

fun createCronDateTimeScheduler(incoming: String): CronDateTimeScheduler {
    val (secondsSource, minutesSource, hoursSource, dayOfMonthSource, monthSource) = incoming.split(" ")

    val secondsParsed = parseSeconds(secondsSource)
    val minutesParsed = parseMinutes(minutesSource)
    val hoursParsed = parseHours(hoursSource)
    val dayOfMonthParsed = parseDaysOfMonth(dayOfMonthSource)
    val monthParsed = parseMonths(monthSource)

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

    return CronDateTimeScheduler(resultCronDateTimes.toList())
}
