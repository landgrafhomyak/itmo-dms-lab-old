package io.github.landgrafhomyak.itmo.dms_lab.v444588.model.requests

import io.github.landgrafhomyak.itmo.dms_lab.interop.DisplayName
import io.github.landgrafhomyak.itmo.dms_lab.lifecycle.ExecutionContext
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import io.github.landgrafhomyak.itmo.dms_lab.requests.RequestMeta
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.AbstractLabWorkCollection
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.LabWork
import kotlinx.serialization.Serializable

@Serializable
public class AddIfMaxRequest(
    @DisplayName("Лабораторная работа")
    private val lw: LabWork
) : BoundRequest<AbstractLabWorkCollection, LabWork> {
    override val meta: RequestMeta
        get() = AddIfMaxRequest

    override suspend fun ExecutionContext<AbstractLabWorkCollection, LabWork>.execute() {
        this.log.debug("Добавление новой лабораторной работы с максимальным условием...")
        val r = this.collection.addIfMax(this@AddIfMaxRequest.lw)
        when (r) {
            is AbstractLabWorkCollection.AddIfMaxResult.EMPTY_COLLECTION -> {
                this.log.debug("Коллекция пуста, элемент добавлен с идентификатором ${r.newId}, отправление сообщения...")
                this.out.info("Коллекция пуста, элемент добавлен с идентификатором ${r.newId}")
            }
            is AbstractLabWorkCollection.AddIfMaxResult.NOT_GREATEST     -> {
                this.log.debug("Коллекция не самый большой, поэтому не добавлен, отправление сообщения...")
                this.log.info("Коллекция не самый большой, поэтому не добавлен")
            }
            is AbstractLabWorkCollection.AddIfMaxResult.GREATEST         -> {
                this.log.debug("Элемент самый большой, поэтому добавлен с идентификатором ${r.newId}, отправление сообщения...")
                this.out.info("Элемент самый большой, поэтому добавлен с идентификатором ${r.newId}")
            }
        }
        this.log.debug("Сообщение об условном добавлении объекта отправлено")
    }

    public companion object Meta : RequestMeta {
        override val description: String = "Добавляет лабораторную работу в коллекцию, если повезёт"

        override val consoleName: String = "add_if_max"
    }
}