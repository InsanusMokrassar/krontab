package com.github.insanusmokrassar.krontab

import com.soywiz.klock.DateTime
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        var i = 0
        executeInfinity("/10 /25 * * *") {
            println(DateTime.now())
            i++
        }
    }
}
