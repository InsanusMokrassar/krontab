package dev.inmo.krontab.utils

import com.soywiz.klock.*
import kotlin.math.min

fun DateTime.copy(
    year: Int = yearInt,
    month: Int = month1,
    dayOfMonth: Int = this.dayOfMonth,
    hour: Int = hours,
    minute: Int = minutes,
    second: Int = seconds,
    milliseconds: Int = this.milliseconds
) = DateTime(
    year,
    month,
    min(Month(month).days(yearInt), dayOfMonth),
    hour,
    minute,
    second,
    milliseconds
)
