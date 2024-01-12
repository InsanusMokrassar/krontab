package dev.inmo.krontab

import android.content.Context
import androidx.work.*
import korlibs.time.DateTime
import korlibs.time.millisecondsLong
import java.util.concurrent.TimeUnit

/**
 * This method will enqueue [OneTimeWorkRequest] with [workName] and [existingWorkPolicy]. Use [setUpRequest] callback
 * in case you need some additional actions to do before request will be enqueued
 */
suspend fun <T : KronSchedulerWork> Context.enqueueKronSchedulerWork(
    workName: String,
    delayMillis: Long,
    workClass: Class<T>,
    existingWorkPolicy: ExistingWorkPolicy = ExistingWorkPolicy.REPLACE,
    setUpRequest: suspend OneTimeWorkRequest.Builder.() -> Unit = {}
) = WorkManager.getInstance(applicationContext).enqueueUniqueWork(
    workName,
    existingWorkPolicy,
    OneTimeWorkRequest.Builder(workClass).apply {
        setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
        setUpRequest()
    }.build()
)
/**
 * This method is shortcut for [enqueueKronSchedulerWork] with reified [T] parameter
 */
suspend inline fun <reified T : KronSchedulerWork> Context.enqueueKronSchedulerWork(
    workName: String,
    delayMillis: Long,
    existingWorkPolicy: ExistingWorkPolicy = ExistingWorkPolicy.REPLACE,
    noinline setUpRequest: suspend OneTimeWorkRequest.Builder.() -> Unit = {}
) = enqueueKronSchedulerWork(workName, delayMillis, T::class.java, existingWorkPolicy, setUpRequest)

/**
 * This method is shortcut for [enqueueKronSchedulerWork] with [initialScheduler]. It will try to calculate delay by
 * itself. In case if [KronScheduler.next] of [initialScheduler] will return null, work WILL NOT be enqueued
 *
 * @return null in case if [KronScheduler.next] of [initialScheduler] has returned null and work has not been enqueued
 */
suspend fun <T : KronSchedulerWork> Context.enqueueKronSchedulerWork(
    workName: String,
    initialScheduler: KronScheduler,
    workClass: Class<T>,
    existingWorkPolicy: ExistingWorkPolicy = ExistingWorkPolicy.REPLACE,
    setUpRequest: suspend OneTimeWorkRequest.Builder.() -> Unit = {}
): Operation? {
    val now = DateTime.now()
    val nextTriggerTime = initialScheduler.next(now)
    val delayMillis = nextTriggerTime ?.minus(now) ?.millisecondsLong ?: return null

    return enqueueKronSchedulerWork(workName, delayMillis, workClass, existingWorkPolicy, setUpRequest)
}
/**
 * This method is shortcut for [enqueueKronSchedulerWork] with reified [T]
 */
suspend inline fun <reified T : KronSchedulerWork> Context.enqueueKronSchedulerWork(
    workName: String,
    initialScheduler: KronScheduler,
    existingWorkPolicy: ExistingWorkPolicy = ExistingWorkPolicy.REPLACE,
    noinline setUpRequest: suspend OneTimeWorkRequest.Builder.() -> Unit = {}
) = enqueueKronSchedulerWork(workName, initialScheduler, T::class.java, existingWorkPolicy, setUpRequest)

/**
 * Use this class as a super class in case you wish to implement krontab-based enqueuing of works
 *
 * @see enqueueKronSchedulerWork
 * @see KrontabTemplateSchedulerWork
 */
abstract class KronSchedulerWork(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(
    context,
    workerParams
) {
    /**
     * This variable will be used to reorder new work after that one is happen
     *
     * @see prolongOnException
     * @see prolongOnFailure
     * @see prolongOnSuccess
     */
    protected abstract val workName: String

    /**
     * Set this to false in case when this work must not be enqueued after successful complete
     */
    protected open val prolongOnSuccess: Boolean = true
    /**
     * Set this to false in case when this work must not be enqueued after failure complete
     */
    protected open val prolongOnFailure
        get() = prolongOnSuccess
    /**
     * Set this to false in case when this work must not be enqueued after exception happen
     */
    protected open val prolongOnException = false

    /**
     * [KronScheduler] of this method will be used to [prolong] this worker
     */
    protected abstract suspend fun kronScheduler(): KronScheduler?

    /**
     * This method is replacement of [doWork]. It is required to wrap work with [prolong]ing and handling of complete
     * state
     */
    protected abstract suspend fun onWork(): Result

    /**
     * Override this method in case you have some additional settings for future [OneTimeWorkRequest]
     */
    protected open suspend fun OneTimeWorkRequest.Builder.setUpRequest() {}

    /**
     * This method will [enqueueKronSchedulerWork] using [workName], [kronScheduler] and default
     * [ExistingWorkPolicy.REPLACE]. You can call this method in case you want to enqueue work by yourself, but you must
     * be sure that you set up to false [prolongOnSuccess], [prolongOnFailure] and [prolongOnException]
     */
    protected suspend fun prolong() {
        applicationContext.enqueueKronSchedulerWork(
            workName,
            kronScheduler() ?: return,
            this::class.java
        ) {
            setUpRequest()
        }
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
        when (result) {
            is Result.Failure -> if (prolongOnFailure) prolong()
            is Result.Success -> if (prolongOnSuccess) prolong()
        }
        return result
    }
}
