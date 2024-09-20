package dev.inmo.krontab

import kotlin.OptionalExpectation

@OptionalExpectation
internal expect annotation class Language(val value: String, val prefix: String = "", val suffix: String = "")
