package com.github.landgrafhomyak.itmo.dms_lab.commands

import kotlin.jvm.JvmField

@Suppress("SpellCheckingInspection")
/**
 * Базовый класс для объектов команд упакованных вместе с их аргументами
 *
 * Является конечным продуктом разбора и обработки команды, может быть применён к любой коллекции из коробки
 * @property meta ссылка на метакласс
 * @see ApplicableToCollection
 */
sealed class BoundCommand(
    @JvmField
    val meta: CommandMeta
)