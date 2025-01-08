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
 *     * "\\d" -> 2
 *     * "\\*" -> 4
 *     * "F" -> 7
 *     * "f" -> 7
 *     * "L" -> 7
 *     * "l" -> 7
 *     * "/" -> 6
 * 2.
 *     * "\\d" -> 2
 *     * "/" -> 6
 *     * "," -> 1
 *     * "-" -> 3
 *     * "m" -> 9
 *     * "o" -> 10
 *     * "w" -> 10
 * 3.
 *     * "L" -> 7
 *     * "l" -> 7
 *     * "\\d" -> 8
 * 4.
 *     * "/" -> 6
 *     * "," -> 1
 * 5.
 *     * "/" -> 6
 * 6.
 *     * "\\d" -> 8
 *     * "\\*" -> 7
 * 7.
 *     * "," -> 1
 * 8.
 *     * "\\d" -> 8
 *     * "," -> 1
 * 9.
 *     * "s" -> 10 // end of ms
 * 10. Empty, end of parse
 */
private val checkIncomingPartTransitionsMap = listOf(
    listOf( // 0
        Regex("\\d") to 1,
        Regex("\\*") to 3,
        Regex("F") to 6,
        Regex("f") to 6,
        Regex("L") to 6,
        Regex("l") to 6,
        Regex("/") to 5,
    ),
    listOf( // 1
        Regex("\\d") to 1,
        Regex("/") to 5,
        Regex(",") to 0,
        Regex("-") to 2,
        Regex("m") to 8,
        Regex("o") to 9,
        Regex("w") to 9,
    ),
    listOf( // 2
        Regex("L") to 6,
        Regex("l") to 6,
        Regex("\\d") to 7,
    ),
    listOf( // 3
        Regex("/") to 5,
        Regex(",") to 0,
    ),
    listOf( // 4
        Regex("/") to 5,
    ),
    listOf( // 5
        Regex("\\d") to 7,
        Regex("\\*") to 6,
    ),
    listOf( // 6
        Regex(",") to 0,
    ),
    listOf( // 7
        Regex("\\d") to 7,
        Regex(",") to 0,
    ),
    listOf( // 8
        Regex("s") to 9, // end of ms
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

