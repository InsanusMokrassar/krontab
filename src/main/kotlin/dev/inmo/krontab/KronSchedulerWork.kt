package dev.inmo.krontab

import android.content.Context
import androidx.work.*
import com.soywiz.klock.DateTime
import java.util.concurrent.TimeUnit

suspend fun <T : KronSchedulerWork> Context.enqueueKronSchedulerWork(
    workName: String,
    delayMillis: Long,
    workClass: Class<T>,
    setUpRequest: suspend OneTimeWorkRequest.Builder.() -> Unit
) = WorkManager.getInstance(applicationContext).enqueueUniqueWork(
    workName,
    ExistingWorkPolicy.REPLACE,
    OneTimeWorkRequest.Builder(workClass).apply {
        setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
        setUpRequest()
    }.build()
)
suspend inline fun <reified T : KronSchedulerWork> Context.enqueueKronSchedulerWork(
    workName: String,
    delayMillis: Long,
    noinline setUpRequest: suspend OneTimeWorkRequest.Builder.() -> Unit
) = enqueueKronSchedulerWork(workName, delayMillis, T::class.java, setUpRequest)

suspend fun <T : KronSchedulerWork> Context.enqueueKronSchedulerWork(
    workName: String,
    initialScheduler: KronScheduler,
    workClass: Class<T>,
    setUpRequest: suspend OneTimeWorkRequest.Builder.() -> Unit
): Operation? {
    val now = DateTime.now()
    val nextTriggerTime = initialScheduler.next(now)
    val delayMillis = nextTriggerTime ?.minus(now) ?.millisecondsLong ?: return null

    return enqueueKronSchedulerWork(workName, delayMillis, workClass, setUpRequest)
}
suspend inline fun <reified T : KronSchedulerWork> Context.enqueueKronSchedulerWork(
    workName: String,
    initialScheduler: KronScheduler,
    noinline setUpRequest: suspend OneTimeWorkRequest.Builder.() -> Unit
) = enqueueKronSchedulerWork(workName, initialScheduler, T::class.java, setUpRequest)

/**
 * Use this class as an super class in case you wish to implement krontab-based enqueuing of works
 */
abstract class KronSchedulerWork(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(
    context,
    workerParams
) {
    protected abstract val workName: String
    protected open val prolongWork: Boolean = true
    protected open val prolongOnFailure
        get() = prolongWork
    protected open val prolongOnException = false
    protected open val prolongOnNextAbsent = false

    protected abstract suspend fun kronScheduler(): KronScheduler?
    protected abstract suspend fun onWork(): Result
    protected open suspend fun OneTimeWorkRequest.Builder.setUpRequest() {}

    protected suspend fun prolong() {
        val now = DateTime.now()
        val nextTriggerTime = kronScheduler() ?.let {
            if (prolongOnNextAbsent) {
                it.nextOrRelative(now)
            } else {
                it.next(now)
            }
        }
        val delayMillis = nextTriggerTime ?.minus(now) ?.millisecondsLong ?: return

        applicationContext.enqueueKronSchedulerWork(workName, delayMillis, this::class.java) { setUpRequest() }
    }

    override suspend fun doWork(): Result {
        val result = try {
            onWork()
        } catch (e: Throwable) {
            if (prolongOnException) {
                prolong()
            }
            throw e
        }
        if (result !is Result.Failure || prolongOnFailure) {
            prolong()
        }
        return result
    }
}
