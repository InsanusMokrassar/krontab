package dev.inmo.krontab.builder

import korlibs.time.TimezoneOffset
import korlibs.time.minutes
import dev.inmo.krontab.KronScheduler
import dev.inmo.krontab.KronSchedulerTz
import dev.inmo.krontab.internal.*
import dev.inmo.krontab.internal.createKronScheduler
import dev.inmo.krontab.internal.createKronSchedulerWithOffset
import dev.inmo.krontab.utils.Minutes

/**
 * Will help to create an instance of [KronScheduler]
 *
 * @see dev.inmo.krontab.createSimpleScheduler
 */
fun buildSchedule(settingsBlock: SchedulerBuilder.() -> Unit): KronScheduler {
    val builder = SchedulerBuilder()

    builder.settingsBlock()

    return builder.build()
}

/**
 * Will help to create an instance of [KronScheduler]
 *
 * @see dev.inmo.krontab.createSimpleScheduler
 */
fun buildSchedule(
    offset: Minutes,
    settingsBlock: SchedulerBuilder.() -> Unit
): KronSchedulerTz {
    val builder = SchedulerBuilder(offset = offset)

    builder.settingsBlock()

    return builder.build() as KronSchedulerTz
}

class SchedulerBuilder(
    private var seconds: Array<Byte>? = null,
    private var minutes: Array<Byte>? = null,
    private var hours: Array<Byte>? = null,
    private var dayOfMonth: Array<Byte>? = null,
    private var month: Array<Byte>? = null,
    private var year: Array<Int>? = null,
    private var dayOfWeek: Array<Byte>? = null,
    private val offset: Minutes? = null,
    private var milliseconds: Array<Short>? = null
) {
    private fun <I, T : TimeBuilder<I>> callAndReturn(
        initial: Array<I>?,
        builder: T,
        block: T.() -> Unit
    ): List<I>? {
        builder.block()

        val builderValue = builder.build()

        return initial ?.let {
            builderValue ?.let { _ ->
                (it + builderValue).distinct()
            } ?: builderValue
        } ?: builderValue
    }

    /**
     * Starts an milliseconds block
     */
    fun milliseconds(block: MillisecondsBuilder.() -> Unit) {
        milliseconds = callAndReturn(
            milliseconds,
            MillisecondsBuilder(),
            block
        ) ?.toTypedArray()
    }

    /**
     * Starts an seconds block
     */
    fun seconds(block: SecondsBuilder.() -> Unit) {
        seconds = callAndReturn(
            seconds,
            SecondsBuilder(),
            block
        ) ?.toTypedArray()
    }

    /**
     * Starts an minutes block
     */
    fun minutes(block: MinutesBuilder.() -> Unit) {
        minutes = callAndReturn(
            minutes,
            MinutesBuilder(),
            block
        ) ?.toTypedArray()
    }

    /**
     * Starts an hours block
     */
    fun hours(block: HoursBuilder.() -> Unit) {
        hours = callAndReturn(
            hours,
            HoursBuilder(),
            block
        ) ?.toTypedArray()
    }

    /**
     * Starts an days of month block
     */
    fun dayOfMonth(block: DaysOfMonthBuilder.() -> Unit) {
        dayOfMonth = callAndReturn(
            dayOfMonth,
            DaysOfMonthBuilder(),
            block
        ) ?.toTypedArray()
    }

    /**
     * Starts an hours block
     */
    fun dayOfWeek(block: WeekDaysBuilder.() -> Unit) {
        dayOfWeek = callAndReturn(
            dayOfWeek,
            WeekDaysBuilder(),
            block
        ) ?.toTypedArray()
    }

    /**
     * Starts an months block
     */
    fun months(block: MonthsBuilder.() -> Unit) {
        month = callAndReturn(
            month,
            MonthsBuilder(),
            block
        ) ?.toTypedArray()
    }

    /**
     * Starts an year block
     */
    fun years(block: YearsBuilder.() -> Unit) {
        year = callAndReturn(
            year,
            YearsBuilder(),
            block
        ) ?.toTypedArray()
    }

    /**
     * @return Completely built and independent [KronScheduler]
     *
     * @see dev.inmo.krontab.createSimpleScheduler
     * @see dev.inmo.krontab.internal.createKronScheduler
     */
    fun build(): KronScheduler = offset ?.let {
        createKronSchedulerWithOffset(
            seconds,
            minutes,
            hours,
            dayOfMonth,
            month,
            year,
            dayOfWeek,
            TimezoneOffset(it.minutes),
            milliseconds ?: millisecondsArrayDefault
        )
    } ?: createKronScheduler(seconds, minutes, hours, dayOfMonth, month, year, dayOfWeek, milliseconds ?: millisecondsArrayDefault)
}
