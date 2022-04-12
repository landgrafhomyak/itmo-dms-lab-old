package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection
import kotlin.jvm.JvmField

@Suppress("SpellCheckingInspection")
/**
 * Базовый класс для объектов команд упакованных вместе с их аргументами
 *
 * Является конечным продуктом разбора и обработки команды, может быть применён к любой коллекции из коробки
 * @property meta ссылка на метакласс
 */
sealed class BoundCommand(
    @JvmField
    val meta: CommandMeta
) {
    @Suppress("SpellCheckingInspection")
    /**
     * Применяет команду с заданными аргументами к коллекции
     * @param logger логгер для вывода сообщений
     * @param collection коллекция к которой будет применена команда
     */
    abstract fun applyTo(logger: Logger, collection: LabWorksCollection)
}