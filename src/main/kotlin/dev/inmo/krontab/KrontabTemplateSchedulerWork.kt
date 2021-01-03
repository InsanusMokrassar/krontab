package dev.inmo.krontab

import android.content.Context
import androidx.work.*

const val krontabTemplateWorkField = "krontabTemplate"

/**
 * Will [enqueueKronSchedulerWork] with [KronScheduler] from [krontabTemplate] and call [setUpRequest] on setting up
 * [OneTimeWorkRequest.Builder] with [Data] which will be used to [OneTimeWorkRequest.Builder.setInputData] after
 * [setUpRequest] completed
 */
suspend inline fun <reified T : KrontabTemplateSchedulerWork> Context.enqueueKrontabTemplateSchedulerWork(
    workName: String,
    krontabTemplate: KrontabTemplate,
    existingWorkPolicy: ExistingWorkPolicy = ExistingWorkPolicy.REPLACE,
    noinline setUpRequest: suspend OneTimeWorkRequest.Builder.(
        data: Data
    ) -> Unit = {}
) = enqueueKronSchedulerWork(workName, krontabTemplate.toKronScheduler(), T::class.java, existingWorkPolicy) {
    val data = workDataOf(
        krontabTemplateWorkField to krontabTemplate
    )
    setUpRequest(data)
    setInputData(data)
}

/**
 * Extend this class in case you wish to base on [KrontabTemplate]. It will automatically handle request of
 * [kronScheduler] and put it in [setUpRequest]
 */
abstract class KrontabTemplateSchedulerWork(
    context: Context,
    workerParams: WorkerParameters
) : KronSchedulerWork(context, workerParams) {
    /**
     * Will try to get [KrontabTemplate] from [getInputData] by key [krontabTemplateWorkField]
     *
     * @see setUpRequest
     */
    protected val krontabTemplate: KrontabTemplate?
        get() = inputData.getString(krontabTemplateWorkField)

    /**
     * Override this methods instead of old [setUpRequest] in case you wish to set up some work request parameters
     *
     * @param data This parameter will be used to put data inside of [OneTimeWorkRequest.Builder] after this method
     * will be completed
     */
    protected open suspend fun OneTimeWorkRequest.Builder.setUpRequest(data: Data) {}

    /**
     * Will automatically put [krontabTemplate] into work data, call [setUpRequest] with future [Data] object and then
     * call [OneTimeWorkRequest.Builder.setInputData] with that [Data] object
     */
    override suspend fun OneTimeWorkRequest.Builder.setUpRequest() {
        val data = workDataOf(
            krontabTemplateWorkField to krontabTemplate,
        )
        setUpRequest(data)
        setInputData(data)
    }

    /**
     * Will return [KronScheduler] in case if [krontabTemplate] was not null
     */
    override suspend fun kronScheduler(): KronScheduler? = krontabTemplate ?.toKronScheduler()
}
