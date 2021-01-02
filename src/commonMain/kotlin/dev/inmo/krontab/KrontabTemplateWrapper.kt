package dev.inmo.krontab

data class KrontabTemplateWrapper(
    val template: KrontabTemplate
) : KronScheduler by template.toKronScheduler()
