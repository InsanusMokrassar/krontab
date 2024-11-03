package dev.inmo.krontab.internal

typealias Converter<T> = (Int) -> T

internal val intToByteConverter: Converter<Byte> = { it: Int -> it.toByte() }
internal val intToShortConverter: Converter<Short> = { it: Int -> it.toShort() }
internal val intToIntConverter: Converter<Int> = { it: Int -> it }
private fun <T> createSimpleScheduler(from: String, dataRange: IntRange, dataConverter: Converter<T>): List<T>? {
    val things = from.split(",")

    val results = things.flatMap {
        val currentToken = it.lowercase().replace(
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

/**
 * FSM for parsing of incoming data. If at the end of parsing it have non-null state and string is not empty, data passed check
 *
 * 1.
 *      * \\d -> 1
 *      * \\* -> 2
 *      * \\- -> 5
 *      * , -> 1
 *      * m -> 6
 *      * o -> 7
 *      * w -> 7
 * 2.
 *      * / -> 3
 * 3.
 *      * \\d -> 3
 *      * \\* -> 4
 * 4.
 *      * , -> 1
 * 5.
 *      * \\d -> 5
 *      * , -> 1
 * 6.
 *      * s -> 7
 * 7. Empty, end of parse
 */
private val checkIncomingPartTransitionsMap = listOf(
    listOf(
        Regex("\\d") to 0,
        Regex("\\*") to 1,
        Regex("-") to 4,
        Regex(",") to 0,
        Regex("m") to 5,
        Regex("o") to 6,
        Regex("w") to 6,
    ),
    listOf(
        Regex("/") to 2,
    ),
    listOf(
        Regex("\\d") to 2,
        Regex("\\*") to 3,
    ),
    listOf(
        Regex(",") to 0,
    ),
    listOf(
        Regex("\\d") to 4,
        Regex(",") to 0,
    ),
    listOf(
        Regex("s") to 6, // end of ms
    ),
    listOf(), // empty state, end of parsing
)
internal fun checkIncomingPart(part: String): Boolean {
    var i = 0
    var state = checkIncomingPartTransitionsMap[0]
    while (i < part.length) {
        val char = part[i]
        val nextState = state.firstNotNullOfOrNull {
            it.second.takeIf { _ -> it.first.matches("$char") }
        }
        if (nextState == null) return false
        state = checkIncomingPartTransitionsMap[nextState]
        i++
    }

    return part.isNotEmpty()
}

internal fun parseWeekDay(from: String?) = from ?.let { if (it.endsWith("w")) createSimpleScheduler(it.removeSuffix("w"), dayOfWeekRange, intToByteConverter) ?.toTypedArray() else null }
internal fun parseOffset(from: String?) = from ?.let { if (it.endsWith("o")) it.removeSuffix("o").toIntOrNull() else null }
internal fun parseYears(from: String?) = from ?.let { createSimpleScheduler(from, yearRange, intToIntConverter) ?.toTypedArray() }
internal fun parseMonths(from: String) = createSimpleScheduler(from, monthRange, intToByteConverter) ?.toTypedArray()
internal fun parseDaysOfMonth(from: String) = createSimpleScheduler(from, dayOfMonthRange, intToByteConverter) ?.toTypedArray()
internal fun parseHours(from: String) = createSimpleScheduler(from, hoursRange, intToByteConverter) ?.toTypedArray()
internal fun parseMinutes(from: String) = createSimpleScheduler(from, minutesRange, intToByteConverter) ?.toTypedArray()
internal fun parseSeconds(from: String) = createSimpleScheduler(from, secondsRange, intToByteConverter) ?.toTypedArray()
internal fun parseMilliseconds(from: String?) = from ?.let { if (it.endsWith("ms")) createSimpleScheduler(from.removeSuffix("ms"), millisecondsRange, intToShortConverter) ?.toTypedArray() else null }

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

