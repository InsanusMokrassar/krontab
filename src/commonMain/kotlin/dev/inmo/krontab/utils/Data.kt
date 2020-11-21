package dev.inmo.krontab.utils

/**
 * @return [min] in case if [this] less than [min]. Otherwise will check that [max] grant than [this] and return [this]
 * if so or [max] otherwise
 */
internal fun Int.clamp(min: Int, max: Int): Int = if (this < min) min else if (this > max) max else this

/**
 * Wrapper function for [clamp] extension
 */
internal fun Int.clamp(range: IntRange): Int = clamp(range.first, range.last)
