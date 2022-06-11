package io.github.landgrafhomyak.itmo.dms_lab.v444588.model.requests

import io.github.landgrafhomyak.itmo.dms_lab.lifecycle.ExecutionContext
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import io.github.landgrafhomyak.itmo.dms_lab.requests.RequestMeta
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.AbstractLabWorkCollection
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.AbstractLabWorkWithId
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.AbstractLabWorkWithIdSerializer
import kotlinx.serialization.Serializable

@Serializable
public object ShowRequest : BoundRequest<AbstractLabWorkCollection, AbstractLabWorkWithId>, RequestMeta {
    override val meta: RequestMeta
        get() = this

    override suspend fun ExecutionContext<AbstractLabWorkCollection, AbstractLabWorkWithId>.execute() {
        if (this@execute.collection.size == 0u) {
            this.out.info("Коллекция пуста")
        } else {
            for (e in this@execute.collection) {
                this.out.info(e, AbstractLabWorkWithIdSerializer)
            }
        }
    }

    override val description: String = "Выводит все лабораторные работы содержащиеся в коллекции"

    override val consoleName: String = "show"
}