package io.github.landgrafhomyak.itmo.dms_lab.v444588.model.requests

import io.github.landgrafhomyak.itmo.dms_lab.interop.DisplayName
import io.github.landgrafhomyak.itmo.dms_lab.lifecycle.ExecutionContext
import io.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import io.github.landgrafhomyak.itmo.dms_lab.requests.RequestMeta
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.AbstractLabWorkCollection
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.AbstractLabWorkWithId
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.InputLabWork
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.LabWorkId
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.NoMetaLabWork
import io.github.landgrafhomyak.itmo.dms_lab.v444588.model.clearMetaAndSave
import kotlinx.serialization.Serializable

@Serializable
public class UpdateRequest(
    @DisplayName("Идентификатор")
    private val id: LabWorkId,
    @DisplayName("Лабораторная работа")
    private val lw: InputLabWork
) : BoundRequest<AbstractLabWorkCollection, AbstractLabWorkWithId> {
    override val meta: RequestMeta
        get() = UpdateRequest

    override suspend fun ExecutionContext<AbstractLabWorkCollection, AbstractLabWorkWithId>.execute() {
        this.log.debug("Обновление лабораторной работы с идентификатором $id...")
        val old = this.collection.update(this@UpdateRequest.id, this@UpdateRequest.lw)
        if (old == null) {
            this.log.debug("Лабораторной работы с идентификатором $id нет, обновлять нечего, отправление сообщений...")
            this.out.error("Лабораторной работы с идентификатором $id нет, обновлять нечего. Используйте add для добавления новой лабораторной работы")
        } else {
            this.log.debug("Лабораторная работа с идентификатором $id обновлена, отправление сообщений...")
            this.out.info("Лабораторная работа с идентификатором $id обновлена, старое значение:")
            this.out.info(old.clearMetaAndSave(), NoMetaLabWork.serializer())
        }
        this.log.debug("Сообщения об обновлении лабораторной работы с идентификатором $id отправлены")
    }

    public companion object Meta : RequestMeta {
        override val description: String = "Обновляет лабораторную работу с заданным идентификатором в коллекции"

        override val consoleName: String = "update"
    }
}