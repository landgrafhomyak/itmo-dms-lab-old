package io.github.landgrafhomyak.itmo.dms_lab.v444588.model

import io.github.landgrafhomyak.itmo.dms_lab.InstantEpochSecondsSerializer
import io.github.landgrafhomyak.itmo.dms_lab.interop.DisplayName
import io.github.landgrafhomyak.itmo.dms_lab.interop.IgnoreInput
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
@DisplayName("Лабораторная работа")
public class LabWork(
    @DisplayName("Название")
    public val name: String,
    @DisplayName("Координаты")
    public val coordinates: Coordinates,
    @DisplayName("Минимальная точка")
    public val minimalPoint: Long,
    @DisplayName("Максимальная точка")
    public val maximumPoint: Double,
    @DisplayName("Максимальный показатель личных качеств")
    public val personalQualitiesMaximum: Int,
    @DisplayName("Сложность")
    public val difficulty: Difficulty,
    @DisplayName("Автор")
    public val author: Person,
    @Serializable(with = InstantEpochSecondsSerializer::class)
    @DisplayName("Время создания")
    @IgnoreInput
    public val creationDate: Instant = Clock.System.now(),
    @DisplayName("Идентификатор")
    @IgnoreInput
    public val id: LabWorkId = LabWorkId.MIN_VALUE,
) : Comparable<LabWork> {
    override fun compareTo(other: LabWork): Int = this.id.compareTo(other.id)
}

public typealias LabWorkId = ULong

/**
 * Копирует [лабораторную работку][LabWork] и присваивает ей новый [идентификатор][newId]
 */
internal inline fun LabWork.withId(newId: LabWorkId): LabWork {
    @Suppress("LiftReturnOrAssignment")
    if (this.id == newId) return this
    else return LabWork(
        name = this.name,
        coordinates = this.coordinates,
        minimalPoint = this.minimalPoint,
        maximumPoint = this.maximumPoint,
        personalQualitiesMaximum = this.personalQualitiesMaximum,
        difficulty = this.difficulty,
        author = this.author,
        creationDate = this.creationDate,
        id = newId
    )
}
