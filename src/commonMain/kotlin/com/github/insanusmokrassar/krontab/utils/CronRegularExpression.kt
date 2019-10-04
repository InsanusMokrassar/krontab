package com.github.insanusmokrassar.krontab.utils

internal val monthsRegex = Regex("[*]|(((1[01]?)|(\\d))(,((1[10]?)|(\\d))){0,10})")
internal val dayOfMonthRegex = Regex("[*]|(((30?)|([012]?\\d))(,((30?)|([012]?\\d))){0,29})")
internal val hoursRegex = Regex("[*]|(((2[0123]?)|([01]?\\d))(,((2[0123]?)|([01]?\\d))){0,23})")
internal val minutesRegex = Regex("[*]|(([012345]?\\d)(,([012345]?\\d)){0,59})")
internal val secondsRegex = minutesRegex
