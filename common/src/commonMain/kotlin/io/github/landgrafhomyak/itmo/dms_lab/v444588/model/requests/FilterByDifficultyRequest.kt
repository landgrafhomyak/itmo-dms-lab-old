package io.github.landgrafhomyak.itmo.dms_lab.v444588.model.requests

import io.github.landgrafhomyak.itmo.dms_lab.lifecycle.ExecutionContext
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import io.github.landgrafhomyak.itmo.dms_lab.requests.RequestMeta
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.AbstractLabWorkCollection
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.Difficulty
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.LabWork
import kotlinx.serialization.Serializable

@Serializable
public class FilterByDifficultyRequest(private val difficulty: Difficulty) : BoundRequest<AbstractLabWorkCollection, LabWork> {
    override val meta: RequestMeta
        get() = FilterByDifficultyRequest

    override suspend fun ExecutionContext<AbstractLabWorkCollection, LabWork>.execute() {
        this.log.debug("Запрос вывода коллекции с фильтром сложности ${this@FilterByDifficultyRequest.difficulty}...")
        val it = this.collection.filterByDifficulty(this@FilterByDifficultyRequest.difficulty)

        if (!it.hasNext()) {
            this.log.debug("Коллекция пуста")
            this.out.info("Коллекция пуста")
        } else {
            this.log.debug("Пересылка элементов коллекции...")
            this.out.info("Лабораторные работы со сложностью ${this@FilterByDifficultyRequest.difficulty}:")
            for (e in it) {
                this.out.info(e, LabWork.serializer())
            }
            this.log.debug("Все элементы коллекции отосланы")
        }
    }

    public companion object Meta : RequestMeta {
        override val description: String = "Выводит все лабораторные работы с заданной сложностью"

        override val consoleName: String = "filter_by_difficulty"
    }
}