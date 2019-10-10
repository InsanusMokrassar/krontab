package com.insanusmokrassar.krontab.builder

import com.insanusmokrassar.krontab.CronDateTimeScheduler
import com.insanusmokrassar.krontab.internal.CronDateTime
import com.insanusmokrassar.krontab.internal.fillWith

fun buildSchedule(settingsBlock: SchedulerBuilder.() -> Unit): CronDateTimeScheduler {
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

    fun seconds(block: SecondsBuilder.() -> Unit) {
        seconds = callAndReturn(
            seconds,
            SecondsBuilder(),
            block
        )
    }

    fun minutes(block: MinutesBuilder.() -> Unit) {
        minutes = callAndReturn(
            minutes,
            MinutesBuilder(),
            block
        )
    }

    fun hours(block: HoursBuilder.() -> Unit) {
        hours = callAndReturn(
            hours,
            HoursBuilder(),
            block
        )
    }

    fun dayOfMonth(block: DaysOfMonthBuilder.() -> Unit) {
        dayOfMonth = callAndReturn(
            dayOfMonth,
            DaysOfMonthBuilder(),
            block
        )
    }

    fun months(block: MonthsBuilder.() -> Unit) {
        month = callAndReturn(
            month,
            MonthsBuilder(),
            block
        )
    }

    fun build(): CronDateTimeScheduler {
        val resultCronDateTimes = mutableListOf(CronDateTime())

        seconds ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
            previousCronDateTime.copy(seconds = currentTime)
        }

        minutes ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
            previousCronDateTime.copy(minutes = currentTime)
        }

        hours ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
            previousCronDateTime.copy(hours = currentTime)
        }

        dayOfMonth ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
            previousCronDateTime.copy(dayOfMonth = currentTime)
        }

        month ?.fillWith(resultCronDateTimes) { previousCronDateTime: CronDateTime, currentTime: Byte ->
            previousCronDateTime.copy(month = currentTime)
        }

        return CronDateTimeScheduler(resultCronDateTimes.toList())
    }
}
