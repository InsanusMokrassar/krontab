package dev.inmo.krontab.utils.flows

import korlibs.time.*
import kotlinx.coroutines.flow.Flow

@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTime>.onlyStartsOfMinutes() = filterSeconds(0)

@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTime>.onlyStartsOfHours() = filterMinutes(0).onlyStartsOfMinutes()

@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTime>.onlyStartsOfDays() = filterHours(0).onlyStartsOfHours()

@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTime>.onlyStartsOfMondays() = filterWeekDays(DayOfWeek.Monday).onlyStartsOfDays()

@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTime>.onlyStartsOfSundays() = filterWeekDays(DayOfWeek.Sunday).onlyStartsOfDays()

@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTime>.onlyStartsOfMonths() = filterDaysOfMonths(1).onlyStartsOfDays()

@Suppress("NOTHING_TO_INLINE")
inline fun Flow<DateTime>.onlyStartsOfYears() = filterMonths(Month.January).onlyStartsOfMonths()
