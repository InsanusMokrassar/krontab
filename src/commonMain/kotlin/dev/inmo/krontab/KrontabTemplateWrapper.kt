package dev.inmo.krontab

/**
 * This class contains [template] and can be simply serialized/deserialized. In fact that class will work as
 * [dev.inmo.krontab.internal.CronDateTimeScheduler] due to the fact that [toKronScheduler] will return it under the
 * hood
 */
data class KrontabTemplateWrapper(
    val template: KrontabTemplate
) : KronScheduler by template.toKronScheduler()

/**
 * Will create [KrontabTemplateWrapper] from [this] [KrontabTemplate]
 *
 * @see [toKronScheduler]
 * @see [KrontabTemplateWrapper]
 */
fun KrontabTemplate.wrapAsKronScheduler() = KrontabTemplateWrapper(this)
