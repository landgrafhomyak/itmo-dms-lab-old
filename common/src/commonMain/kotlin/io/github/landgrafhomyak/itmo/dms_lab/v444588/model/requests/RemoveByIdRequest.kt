package io.github.landgrafhomyak.itmo.dms_lab.v444588.model.requests

import io.github.landgrafhomyak.itmo.dms_lab.interop.DisplayName
import io.github.landgrafhomyak.itmo.dms_lab.lifecycle.ExecutionContext
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import io.github.landgrafhomyak.itmo.dms_lab.requests.RequestMeta
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.AbstractLabWorkCollection
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.LabWork
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.LabWorkId
import kotlinx.serialization.Serializable

@Serializable
public class RemoveByIdRequest(
    @DisplayName("Идентификатор")
    private val id: LabWorkId,
) : BoundRequest<AbstractLabWorkCollection, LabWork> {
    override val meta: RequestMeta
        get() = RemoveByIdRequest

    override suspend fun ExecutionContext<AbstractLabWorkCollection, LabWork>.execute() {
        this.log.debug("Удаление лабораторной работы с идентификатором $id...")
        val removed = this.collection.removeById(this@RemoveByIdRequest.id)
        if (removed == null) {
            this.log.debug("Лабораторной работы с идентификатором $id нет, удалять нечего, отправление сообщений...")
            this.out.error("Лабораторной работы с идентификатором $id нет, удалять нечего")
        } else {
            this.log.debug("Лабораторная работа с идентификатором $id удалена, отправление сообщений...")
            this.out.info("Лабораторная работа с идентификатором $id удалена, её содержимое:")
            this.out.info(removed, LabWork.serializer())
        }
        this.log.debug("Сообщения об удалении лабораторной работы с идентификатором $id отправлены")
    }

    public companion object Meta : RequestMeta {
        override val description: String = "Удаляет лабораторную работу с заданным идентификатором из коллекции"

        override val consoleName: String = "remove_by_id"
    }
}