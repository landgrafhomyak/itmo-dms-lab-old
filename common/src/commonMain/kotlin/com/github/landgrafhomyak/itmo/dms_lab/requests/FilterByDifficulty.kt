package com.github.landgrafhomyak.itmo.dms_lab.requests

import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import com.github.landgrafhomyak.itmo.dms_lab.objects.Difficulty
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorksCollection

/**
 * Конечный объект запроса `filter_by_difficulty`
 * @param difficulty ключ, по которому будут выбраны элементы
 * @sample FilterByDifficulty.help
 */
@Suppress("unused", "EqualsOrHashCode")
class FilterByDifficulty(
    @Suppress("MemberVisibilityCanBePrivate")
    val difficulty: Difficulty,
) : BoundRequest(), ApplicableToCollection {
    override val meta: RequestMeta
        get() = Meta

    companion object Meta : RequestMeta {
        override val id: String = "filter_by_difficulty"
        override val help: String = "Выводит элементы, значение поля difficulty которых равно заданному"
    }

    override suspend fun applyTo(logger: Logger, collection: LabWorksCollection) {
        for (elem in collection.getByDifficulty(this.difficulty)) {
            logger.sendObject(elem)
        }
    }



    @Suppress("CovariantEquals")
    override fun equals(other: BoundRequest): Boolean {
        if (other !is FilterByDifficulty) return false
        return this.difficulty == other.difficulty
    }

    override fun hashCode(): Int = this.difficulty.hashCode()
}