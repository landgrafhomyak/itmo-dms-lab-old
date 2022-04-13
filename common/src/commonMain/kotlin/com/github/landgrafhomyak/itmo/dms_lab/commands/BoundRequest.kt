package com.github.landgrafhomyak.itmo.dms_lab.commands

import kotlin.jvm.JvmField

@Suppress("SpellCheckingInspection")
/**
 * Базовый класс для объектов запросов упакованных вместе с их аргументами
 *
 * Является конечным продуктом разбора и обработки запроса, может быть применён к любой коллекции из коробки
 * @property meta ссылка на метаобъект
 * @see ApplicableToCollection
 */
abstract class BoundRequest(
    @JvmField
    val meta: RequestMeta
)