package io.github.landgrafhomyak.itmo.dms_lab.v444588.model

import io.github.landgrafhomyak.itmo.dms_lab.interop.DisplayName
import kotlinx.datetime.Instant

@DisplayName("Лабораторная работа")
public interface AbstractLabWork: Comparable<AbstractLabWork> {
    @DisplayName("Название")
    public val name: String

    @DisplayName("Координаты")
    public val coordinates: Coordinates

    @DisplayName("Время создания")
    public val creationDate: Instant

    @DisplayName("Минимальная точка")
    public val minimalPoint: Long

    @DisplayName("Максимальная точка")
    public val maximumPoint: Double

    @DisplayName("Максимальный показатель личных качеств")
    public val personalQualitiesMaximum: Int

    @DisplayName("Сложность")
    public val difficulty: Difficulty

    @DisplayName("Автор")
    public val author: Person

    public companion object {
        @Suppress("unused")
        internal inline fun validate(o: AbstractLabWork) = o.validate()
    }

    override fun compareTo(other: AbstractLabWork): Int {
        return this.creationDate.compareTo(other.creationDate)
    }
}

internal inline fun AbstractLabWork.validate() {
    require(this.name.isNotEmpty()) { "Название лабораторной работы должно быть непустым" }
    require(this.minimalPoint > 0) { "Минимальная точка должна быть положительной" }
    require(this.minimalPoint > 0) { "Максимальная точка должна быть положительной" }
    require(this.personalQualitiesMaximum > 0) { "Максимальный показатель личных качеств должен быть положительным" }
}