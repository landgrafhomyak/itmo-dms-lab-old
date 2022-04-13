package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.Difficulty
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Конечный объект запроса `filter_by_difficulty`
 * @param difficulty ключ, по которому будут выбраны элементы
 * @sample FilterByDifficulty.help
 */
@Suppress("unused")
class FilterByDifficulty(
    @Suppress("MemberVisibilityCanBePrivate")
    val difficulty: Difficulty,
) : BoundRequest(Meta), ApplicableToCollection {
    companion object Meta : RequestMeta {
        override val id: String = "filter_by_difficulty"
        override val help: String = "Выводит элементы, значение поля difficulty которых равно заданному"
    }

    override suspend fun applyTo(logger: Logger, collection: LabWorksCollection) {
        for (elem in collection.getByDifficulty(this.difficulty)) {
            logger.sendObject(elem)
        }
    }
}