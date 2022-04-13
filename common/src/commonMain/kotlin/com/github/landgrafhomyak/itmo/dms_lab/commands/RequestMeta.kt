package com.github.landgrafhomyak.itmo.dms_lab.commands

@Suppress("SpellCheckingInspection", "GrazieInspection")
/**
 * Метаинформация о запросе, доступная без создания конечного объекта
 *
 * Аналог интерфейса для метаклассов
 */
interface RequestMeta {
    /**
     * Уникальный идентификатор запроса
     */
    val id: String

    /**
     * Краткая информация о запросе
     */
    val help: String
}