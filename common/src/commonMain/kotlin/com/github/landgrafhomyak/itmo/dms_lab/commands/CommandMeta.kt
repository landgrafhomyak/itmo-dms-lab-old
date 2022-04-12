package com.github.landgrafhomyak.itmo.dms_lab.commands

@Suppress("SpellCheckingInspection", "GrazieInspection")
/**
 * Метаинформация о команде, доступная без создания конечного объекта
 *
 * Аналог интерфейса для метаклассов
 */
sealed class CommandMeta {
    /**
     * Уникальный идентификатор команды
     */
    abstract val id: String

    /**
     * Краткая информация о команде
     */
    abstract val help: String
}