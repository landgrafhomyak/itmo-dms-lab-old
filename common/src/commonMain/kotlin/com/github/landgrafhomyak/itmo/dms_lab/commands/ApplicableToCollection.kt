package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Показывает, что команда может быть применена к [коллекции][LabWorksCollection]
 */
interface ApplicableToCollection {
    @Suppress("SpellCheckingInspection")
    /**
     * Применяет команду с заданными аргументами к коллекции
     * @param logger логгер для вывода сообщений
     * @param collection коллекция к которой будет применена команда
     */
    suspend fun applyTo(logger: Logger, collection: LabWorksCollection)
}