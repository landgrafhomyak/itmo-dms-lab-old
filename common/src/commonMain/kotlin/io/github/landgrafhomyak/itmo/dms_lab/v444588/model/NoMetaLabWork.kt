package io.github.landgrafhomyak.itmo.dms_lab.v444588.model

import io.github.landgrafhomyak.itmo.dms_lab.InstantEpochSecondsSerializer
import io.github.landgrafhomyak.itmo.dms_lab.interop.DisplayName
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
@DisplayName("Лабораторная работа")
public class NoMetaLabWork(
    @DisplayName("Название")
    override val name: String,
    @DisplayName("Координаты")
    override val coordinates: Coordinates,
    @Serializable(with = InstantEpochSecondsSerializer::class)
    @DisplayName("Время создания")
    override val creationDate: Instant = Clock.System.now(),
    @DisplayName("Минимальная точка")
    override val minimalPoint: Long,
    @DisplayName("Максимальный показатель личных качеств")
    override val maximumPoint: Double,
    @DisplayName("Максимальный показатель личных качеств")
    override val personalQualitiesMaximum: Int,
    @DisplayName("Сложность")
    override val difficulty: Difficulty,
    @DisplayName("Автор")
    override val author: Person
) : AbstractLabWork

public typealias InputLabWork = NoMetaLabWork

public inline fun AbstractLabWork.clearMetaAndSave(): NoMetaLabWork =
    if (this is NoMetaLabWork) this
    else NoMetaLabWork(
        this.name,
        this.coordinates,
        this.creationDate,
        this.minimalPoint,
        this.maximumPoint,
        this.personalQualitiesMaximum,
        this.difficulty,
        this.author
    )