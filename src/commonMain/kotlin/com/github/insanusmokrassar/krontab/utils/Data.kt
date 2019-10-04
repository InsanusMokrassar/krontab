package com.github.insanusmokrassar.krontab.utils

internal fun Int.clamp(min: Int, max: Int): Int = if (this < min) min else if (this > max) max else this
internal fun Int.clamp(range: IntRange): Int = clamp(range.first, range.last)
