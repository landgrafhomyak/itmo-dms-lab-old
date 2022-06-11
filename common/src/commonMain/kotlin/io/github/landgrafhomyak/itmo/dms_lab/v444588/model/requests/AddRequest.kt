package io.github.landgrafhomyak.itmo.dms_lab.v444588.model.requests

import io.github.landgrafhomyak.itmo.dms_lab.interop.DisplayName
import io.github.landgrafhomyak.itmo.dms_lab.lifecycle.ExecutionContext
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import io.github.landgrafhomyak.itmo.dms_lab.requests.RequestMeta
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.AbstractLabWorkCollection
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.LabWork
import kotlinx.serialization.Serializable

@Serializable
public class AddRequest(
    @DisplayName("Лабораторная работа")
    private val lw: LabWork
) : BoundRequest<AbstractLabWorkCollection, LabWork> {
    override val meta: RequestMeta
        get() = AddRequest

    override suspend fun ExecutionContext<AbstractLabWorkCollection, LabWork>.execute() {
        this.log.debug("Добавление новой лабораторной работы...")
        val id = this.collection.add(this@AddRequest.lw)
        this.log.debug("Лабораторная работа добавлена с идентификатором $id, отправление сообщения...")
        this.out.info("Лабораторная работа добавлена с идентификатором $id")
        this.log.debug("Сообщение о добавлении лабораторной работы с сообщением $id отправлено")
    }

    public companion object Meta : RequestMeta {
        override val description: String = "Добавляет лабораторную работу в коллекцию"

        override val consoleName: String = "add"
    }
}