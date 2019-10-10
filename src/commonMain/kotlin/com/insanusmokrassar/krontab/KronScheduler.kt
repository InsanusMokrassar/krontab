package com.insanusmokrassar.krontab

import com.soywiz.klock.DateTime

interface KronScheduler {
    suspend fun next(relatively: DateTime = DateTime.now()): DateTime
}
