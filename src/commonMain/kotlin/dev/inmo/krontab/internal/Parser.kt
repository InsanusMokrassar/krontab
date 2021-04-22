package dev.inmo.krontab.internal

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
                (splitted.first().toInt().coerceIn(dataRange) .. splitted[1].toInt().coerceIn(dataRange)).toList()
            }
            currentToken.contains("/") -> {
                val (start, step) = currentToken.split("/")
                val startNum = (if (start.isEmpty() || start == "*") {
                    0
                } else {
                    start.toInt()
                }).coerceIn(dataRange)
                val stepNum = step.toInt().coerceIn(dataRange)
                (startNum .. dataRange.last step stepNum).map { it }
            }
            currentToken == "*" -> return null
            else -> listOf(currentToken.toInt().coerceIn(dataRange))
        }
    }

    return results.map(dataConverter)
}

internal fun parseWeekDay(from: String?) = from ?.let { if (it.endsWith("w")) createSimpleScheduler(it.removeSuffix("w"), dayOfWeekRange, intToByteConverter) ?.toTypedArray() else null }
internal fun parseOffset(from: String?) = from ?.let { if (it.endsWith("o")) it.removeSuffix("o").toIntOrNull() else null }
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

internal fun <T> T.fillWith(
    whereToPut: MutableList<CronDateTime>,
    createFactory: (CronDateTime, T) -> CronDateTime
) {
    val previousValues = whereToPut.toList()

    whereToPut.clear()

    previousValues.forEach { previousValue ->
        whereToPut.add(createFactory(previousValue, this))
    }
}

