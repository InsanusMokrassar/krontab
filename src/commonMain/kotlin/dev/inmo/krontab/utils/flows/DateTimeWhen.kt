package dev.inmo.krontab.utils.flows

import korlibs.time.*
import kotlinx.coroutines.flow.Flow

@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTimeTz>.onlyStartsOfMinutes() = filterSeconds(0)

@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTimeTz>.onlyStartsOfHours() = filterMinutes(0).onlyStartsOfMinutes()

@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTimeTz>.onlyStartsOfDays() = filterHours(0).onlyStartsOfHours()

@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTimeTz>.onlyStartsOfMondays() = filterWeekDays(DayOfWeek.Monday).onlyStartsOfDays()

@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTimeTz>.onlyStartsOfSundays() = filterWeekDays(DayOfWeek.Sunday).onlyStartsOfDays()

@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTimeTz>.onlyStartsOfMonths() = filterDaysOfMonths(1).onlyStartsOfDays()

@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTimeTz>.onlyStartsOfYears() = filterMonths(Month.January).onlyStartsOfMonths()
