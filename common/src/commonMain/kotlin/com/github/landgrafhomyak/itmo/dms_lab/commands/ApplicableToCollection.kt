package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Показывает, что запрос может быть применён к [коллекции][LabWorksCollection]
 */
interface ApplicableToCollection {
    @Suppress("SpellCheckingInspection")
    /**
     * Применяет запрос с упакованными в объекте аргументами к коллекции
     * @param logger логгер для вывода сообщений
     * @param collection коллекция к которой будет применена запрос
     */
    suspend fun applyTo(logger: Logger, collection: LabWorksCollection)
}