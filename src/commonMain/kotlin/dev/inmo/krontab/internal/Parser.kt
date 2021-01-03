package dev.inmo.krontab.internal

import dev.inmo.krontab.utils.clamp

typealias Converter<T> = (Int) -> T

internal val intToByteConverter: Converter<Byte> = { it: Int -> it.toByte() }
internal val intToIntConverter: Converter<Int> = { it: Int -> it }
private fun <T> createSimpleScheduler(from: String, dataRange: IntRange, dataConverter: Converter<T>): List<T>? {
    val things = from.split(",")

    val results = things.flatMap {
        val currentToken = it.toLowerCase().replace(
            "f", dataRange.first.toString()
        ).replace(
            "l", dataRange.last.toString()
        )
        when {
            currentToken.contains("-") -> {
                val splitted = currentToken.split("-")
                (splitted.first().toInt().clamp(dataRange) .. splitted[1].toInt().clamp(dataRange)).toList()
            }
            currentToken.contains("/") -> {
                val (start, step) = currentToken.split("/")
                val startNum = (if (start.isEmpty() || start == "*") {
                    0
                } else {
                    start.toInt()
                }).clamp(dataRange)
                val stepNum = step.toInt().clamp(dataRange)
                (startNum .. dataRange.last step stepNum).map { it }
            }
            currentToken == "*" -> return null
            else -> listOf(currentToken.toInt().clamp(dataRange))
        }
    }

    return results.map(dataConverter)
}

internal fun parseYears(from: String?) = from ?.let { createSimpleScheduler(from, yearRange, intToIntConverter) ?.toTypedArray() }
internal fun parseMonths(from: String) = createSimpleScheduler(from, monthRange, intToByteConverter) ?.toTypedArray()
internal fun parseDaysOfMonth(from: String) = createSimpleScheduler(from, dayOfMonthRange, intToByteConverter) ?.toTypedArray()
internal fun parseHours(from: String) = createSimpleScheduler(from, hoursRange, intToByteConverter) ?.toTypedArray()
internal fun parseMinutes(from: String) = createSimpleScheduler(from, minutesRange, intToByteConverter) ?.toTypedArray()
internal fun parseSeconds(from: String) = createSimpleScheduler(from, secondsRange, intToByteConverter) ?.toTypedArray()

internal fun <T> Array<T>.fillWith(
    whereToPut: MutableList<CronDateTime>,
    createFactory: (CronDateTime, T) -> CronDateTime
) {
    val previousValues = whereToPut.toList()

    whereToPut.clear()

    previousValues.forEach { previousValue ->
        forEach {
            whereToPut.add(createFactory(previousValue, it))
        }
    }
}

