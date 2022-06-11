package io.github.landgrafhomyak.itmo.dms_lab.v444588.model.requests

import io.github.landgrafhomyak.itmo.dms_lab.lifecycle.ExecutionContext
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import io.github.landgrafhomyak.itmo.dms_lab.requests.RequestMeta
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.AbstractLabWorkCollection
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.LabWork
import kotlinx.serialization.Serializable

@Serializable
public object PrintDescendingRequest : BoundRequest<AbstractLabWorkCollection, LabWork>, RequestMeta {
    override val meta: RequestMeta
        get() = this

    override suspend fun ExecutionContext<AbstractLabWorkCollection, LabWork>.execute() {
        this.log.debug("Запрос вывода коллекции в порядке убывания...")
        if (this@execute.collection.size == 0u) {
            this.log.debug("Коллекция пуста")
            this.out.info("Коллекция пуста")
        } else {
            this.log.debug("Пересылка элементов коллекции в порядке убывания...")
            this.out.info("Содержимое коллекции в порядке убывания:")
            for (e in this@execute.collection.sortFieldDescendingMaximumPoint()) {
                this.out.info(e, LabWork.serializer())
            }
            this.log.debug("Все элементы коллекции отосланы")
        }
    }

    override val description: String = "Выводит все лабораторные работы в порядке убывания"

    override val consoleName: String = "print_descending"
}