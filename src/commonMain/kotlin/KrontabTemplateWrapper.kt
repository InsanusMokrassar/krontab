package dev.inmo.krontab

/**
 * This class contains [template] and can be simply serialized/deserialized. In fact that class will work as
 * [dev.inmo.krontab.internal.CronDateTimeScheduler] due to the fact that [toKronScheduler] will return it under the
 * hood
 */
@Deprecated(
    "It is useless wrapper for KrontabTemplate. Use KrontabConfig instead",
    ReplaceWith("KrontabConfig(template)", "dev.inmo.krontab.KrontabConfig")
)
data class KrontabTemplateWrapper(
    val template: KrontabTemplate
) : KronScheduler by template.toKronScheduler()

/**
 * Will create [KrontabTemplateWrapper] from [this] [KrontabTemplate]
 *
 * @see [toKronScheduler]
 * @see [KrontabTemplateWrapper]
 */
@Deprecated(
    "Will be removed in near major update with KrontabTemplateWrapper",
    ReplaceWith("this.krontabConfig", "dev.inmo.krontab.krontabConfig")
)
fun KrontabTemplate.wrapAsKronScheduler() = KrontabTemplateWrapper(this)
