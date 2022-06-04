package io.github.landgrafhomyak.itmo.dms_lab.v444588.model

import io.github.landgrafhomyak.itmo.dms_lab.interop.DisplayName

@DisplayName("Лабораторная работа")
public interface AbstractLabWorkWithId : AbstractLabWork {
    @DisplayName("Уникальный идентификатор")
    public val id: LabWorkId
}

public typealias LabWorkId = Long