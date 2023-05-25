package dev.inmo.krontab.utils.flows

import korlibs.time.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter

@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTime>.filterSeconds(vararg seconds: Int) = filter { it.seconds in seconds }

@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTime>.filterMinutes(vararg minutes: Int) = filter { it.minutes in minutes }

@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTime>.filterHours(vararg hours: Int) = filter { it.hours in hours }

@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTime>.filterDaysOfMonths(vararg daysOfMonths: Int) = filter { it.dayOfMonth in daysOfMonths }

@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTime>.filterMonths(vararg months: Month) = filter { it.month in months }
@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTime>.filterMonths0(vararg months: Int) = filter { it.month0 in months }
@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTime>.filterMonths1(vararg months: Int) = filter { it.month1 in months }

@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTime>.filterYears(year: Year) = filter { it.year == year }
@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTime>.filterYears(vararg years: Int) = filter { it.yearInt in years }

@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTime>.filterWeekDays(vararg weekDays: DayOfWeek) = filter { it.dayOfWeek in weekDays }
@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTime>.filterWeekDays(vararg weekDays: Int) = filter { it.dayOfWeekInt in weekDays }
