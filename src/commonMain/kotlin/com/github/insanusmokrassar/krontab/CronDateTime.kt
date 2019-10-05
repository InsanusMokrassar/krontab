package com.github.insanusmokrassar.krontab

import com.github.insanusmokrassar.krontab.utils.*
import com.github.insanusmokrassar.krontab.utils.clamp
import com.github.insanusmokrassar.krontab.utils.dayOfMonthRange
import com.github.insanusmokrassar.krontab.utils.monthRange

/**
 * [month] 0-11
 * [dayOfMonth] 0-31
 * [hours] 0-23
 * [minutes] 0-59
 * [seconds] 0-59
 */
internal data class CronDateTime(
    val month: Byte? = null,
    val dayOfMonth: Byte? = null,
    val hours: Byte? = null,
    val minutes: Byte? = null,
    val seconds: Byte? = null
) {
    init {
        check(month ?.let { it in monthRange } ?: true)
        check(dayOfMonth ?.let { it in dayOfMonthRange } ?: true)
        check(hours?.let { it in hoursRange } ?: true)
        check(minutes?.let { it in minutesRange } ?: true)
        check(seconds?.let { it in secondsRange } ?: true)
    }

    internal val klockDayOfMonth = dayOfMonth ?.plus(1)

    companion object {
        fun create(
            month: Int? = null,
            dayOfMonth: Int? = null,
            hours: Int? = null,
            minutes: Int? = null,
            seconds: Int? = null
        ) = CronDateTime(
            month ?.clamp(monthRange) ?.toByte(),
            dayOfMonth ?.clamp(dayOfMonthRange) ?.toByte(),
            hours ?.clamp(hoursRange) ?.toByte(),
            minutes ?.clamp(minutesRange) ?.toByte(),
            seconds ?.clamp(secondsRange) ?.toByte()
        )
    }
}
