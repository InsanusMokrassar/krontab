package com.github.insanusmokrassar.krontab.parts

internal inline fun buildPeriodRegex(periodNumbers: Int): Regex {
    val periodRegex = "\\d{1,$periodNumbers}"
    return Regex("(\\*(/$periodRegex)?)|($periodRegex(-$periodRegex)?)")
}

private fun extractLongTermPeriod(period: String, minPeriodValue: Int, maxPeriodValue: Int): List<Int>? {
    if (period.contains("-")) {
        val (first, second) = period.split("-")
        val firstNumber = first.toInt().let {
            if (it < minPeriodValue) {
                minPeriodValue
            } else {
                it
            }
        }
        val secondNumber = second.toInt().let {
            if (it > maxPeriodValue) {
                maxPeriodValue
            } else {
                it
            }
        }
        return (firstNumber .. secondNumber).toList()
    }
    return null
}

private fun extractPeriods(period: String, minPeriodValue: Int, maxPeriodValue: Int): List<Int>? {
    if (period.startsWith("*")) {
        val splitted = period.split("/")
        when {
            splitted.size > 1 -> {
                val repeatPeriod = splitted[1].toInt()
                return (minPeriodValue .. maxPeriodValue step repeatPeriod).toList()
            }
            else -> {
                (minPeriodValue .. maxPeriodValue).toList()
            }
        }
    }
    return null
}

private val oneTimeRegex = Regex("^\\d*$")
private fun extractOneTime(period: String, minPeriodValue: Int, maxPeriodValue: Int): List<Int>? {
    oneTimeRegex.find(period) ?.let {
        val found = it.groupValues.firstOrNull() ?: return null
        val foundAsInt = found.toInt()
        val resultTime = when {
            minPeriodValue > foundAsInt -> minPeriodValue
            maxPeriodValue < foundAsInt -> maxPeriodValue
            else -> foundAsInt
        }
        return listOf(resultTime)
    }
    return null
}

private fun adaptPeriod(period: String, minPeriodValue: Int, maxPeriodValue: Int): List<Int> {
    return extractLongTermPeriod(
        period,
        minPeriodValue,
        maxPeriodValue
    ) ?: extractPeriods(
        period,
        minPeriodValue,
        maxPeriodValue
    ) ?: extractOneTime(
        period,
        minPeriodValue,
        maxPeriodValue
    ) ?: emptyList()
}

internal fun getTimes(
    period: String,
    minPeriodValue: Int,
    maxPeriodValue: Int,
    unconfinedVariants: Map<String, Int> = emptyMap()
): List<Int> {
    return period.split(",").flatMap {
        unconfinedVariants[it] ?.let {
            listOf(it)
        } ?: adaptPeriod(
            it,
            minPeriodValue,
            maxPeriodValue
        )
    }
}

