package com.insanusmokrassar.krontab.builder

import com.insanusmokrassar.krontab.KronScheduler
import com.insanusmokrassar.krontab.internal.*
import com.insanusmokrassar.krontab.internal.CronDateTime
import com.insanusmokrassar.krontab.internal.CronDateTimeScheduler
import com.insanusmokrassar.krontab.internal.fillWith

/**
 * Will help to create an instance of [KronScheduler]
 *
 * @see com.insanusmokrassar.krontab.createSimpleScheduler
 */
fun buildSchedule(settingsBlock: SchedulerBuilder.() -> Unit): KronScheduler {
    val builder = SchedulerBuilder()

    builder.settingsBlock()

    return builder.build()
}

class SchedulerBuilder(
    private var seconds: Array<Byte>? = null,
    private var minutes: Array<Byte>? = null,
    private var hours: Array<Byte>? = null,
    private var dayOfMonth: Array<Byte>? = null,
    private var month: Array<Byte>? = null
) {
    private fun <T : TimeBuilder> callAndReturn(
        initial: Array<Byte>?,
        builder: T,
        block: T.() -> Unit
    ): Array<Byte>? {
        builder.block()

        val builderValue = builder.build()

        return initial ?.let {
            builderValue ?.let { _ ->
                (it + builderValue).distinct().toTypedArray()
            } ?: builderValue
        } ?: builderValue
    }

    /**
     * Starts an seconds block
     */
    fun seconds(block: SecondsBuilder.() -> Unit) {
        seconds = callAndReturn(
            seconds,
            SecondsBuilder(),
            block
        )
    }

    /**
     * Starts an minutes block
     */
    fun minutes(block: MinutesBuilder.() -> Unit) {
        minutes = callAndReturn(
            minutes,
            MinutesBuilder(),
            block
        )
    }

    /**
     * Starts an hours block
     */
    fun hours(block: HoursBuilder.() -> Unit) {
        hours = callAndReturn(
            hours,
            HoursBuilder(),
            block
        )
    }

    /**
     * Starts an days of month block
     */
    fun dayOfMonth(block: DaysOfMonthBuilder.() -> Unit) {
        dayOfMonth = callAndReturn(
            dayOfMonth,
            DaysOfMonthBuilder(),
            block
        )
    }

    /**
     * Starts an months block
     */
    fun months(block: MonthsBuilder.() -> Unit) {
        month = callAndReturn(
            month,
            MonthsBuilder(),
            block
        )
    }

    /**
     * @return Completely built and independent [KronScheduler]
     *
     * @see com.insanusmokrassar.krontab.createSimpleScheduler
     * @see com.insanusmokrassar.krontab.internal.createKronScheduler
     */
    fun build(): KronScheduler = createKronScheduler(seconds, minutes, hours, dayOfMonth, month)
}
