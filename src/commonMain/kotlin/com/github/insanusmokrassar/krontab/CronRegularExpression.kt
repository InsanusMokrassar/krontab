package com.github.insanusmokrassar.krontab.utils

internal val monthNumberRegex = Regex("(1[01]?)|(\\d)")
internal val dayOfMonthNumberRegex = Regex("(30?)|([012]?\\d)")
internal val hoursNumberRegex = Regex("(2[0123]?)|([01]?\\d)")
internal val minutesOrSecondsNumberRegex = Regex("[012345]?\\d")

internal val monthRegex = Regex("[*]|((${monthNumberRegex.pattern})(,(${monthNumberRegex.pattern})){0,10})")
internal val dayOfMonthRegex = Regex("[*]|((${dayOfMonthNumberRegex.pattern})(,(${dayOfMonthNumberRegex.pattern})){0,29})")
internal val hoursRegex = Regex("[*]|((${hoursNumberRegex.pattern})(,(${hoursNumberRegex.pattern})){0,23})")
internal val minutesRegex = Regex("[*]|((${minutesOrSecondsNumberRegex.pattern})(,(${minutesOrSecondsNumberRegex.pattern})){0,59})")
internal val secondsRegex = minutesRegex

internal val monthRange = 0 .. 11
internal val dayOfMonthRange = 0 .. 30
internal val hoursRange = 0 .. 23
internal val minutesRange = 0 .. 59
internal val secondsRange = minutesRange
