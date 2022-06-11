package io.github.landgrafhomyak.itmo.dms_lab.v444588.model.requests

import io.github.landgrafhomyak.itmo.dms_lab.lifecycle.ExecutionContext
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import io.github.landgrafhomyak.itmo.dms_lab.requests.RequestMeta
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.AbstractLabWorkCollection
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.LabWork
import kotlinx.serialization.Serializable

@Serializable
public object ClearRequest : BoundRequest<AbstractLabWorkCollection, LabWork>, RequestMeta {
    override val meta: RequestMeta
        get() = this

    override suspend fun ExecutionContext<AbstractLabWorkCollection, LabWork>.execute() {
        this.log.debug("Запрос очищения коллекции...")
        this.collection.clear()
        this.log.debug("Коллекция очищена, отправка сообщения...")
        this.out.info("Коллекция очищена")
        this.log.debug("Коллекция очищена, сообщение отправлено")
    }

    override val description: String = "Удаляет все лабораторные работы из коллекции"

    override val consoleName: String = "clear"
}