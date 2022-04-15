package com.github.landgrafhomyak.itmo.dms_lab.objects

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Данные о лабораторной работе
 * @property name название ( `isNotBlank` )
 * @property coordinates координаты
 * @property minimalPoint минимальная точка ( `> 0` )
 * @property maximumPoint максимальная точка ( `> 0` )
 * @property personalQualitiesMaximum максимальный показатель личных качеств ( `> 0` )
 * @property difficulty [сложность][Difficulty]
 * @property author [автор][Person]
 */
@Suppress("KDocUnresolvedReference", "EqualsOrHashCode", "unused")
data class LabWork(
    val name: String,
    val coordinates: Coordinates,
    val minimalPoint: Long,
    val maximumPoint: Double,
    val personalQualitiesMaximum: Int,
    val difficulty: Difficulty,
    val author: Person
) {
    /**
     * Дата создания объекта. Может быть перезаписано при чтении из внешней базы данных
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var creationDate: Instant = Clock.System.now()

    /**
     * Идентификатор объекта в [коллекции][LabWorksCollection]. Если равно `null`, то объект
     * свободен и может быть добавлен в [коллекцию][LabWorksCollection], иначе - занят и при
     * попытке добавления будет получена ошибка
     */
    var id: LabWorkId? = null
        internal set

    init {
        if (this.name.isBlank()) throw FailedCreateObjectException("Название лабораторной работы должно быть непустым")
        if (this.minimalPoint <= 0) throw FailedCreateObjectException("Минимальная точка должна быть строго положительной")
        if (this.maximumPoint <= 0) throw FailedCreateObjectException("Максимальная точка должна быть строго положительной")
        if (this.personalQualitiesMaximum <= 0) throw FailedCreateObjectException("Показатель личных качеств должен быть строго положительной")
    }

    /**
     * Создаёт копию лабораторной работы, не привязанную ни к какой [коллекции][LabWorksCollection]
     */
    fun copy() = LabWork(
        name = this.name,
        coordinates = this.coordinates,
        minimalPoint = this.minimalPoint,
        maximumPoint = this.maximumPoint,
        personalQualitiesMaximum = this.personalQualitiesMaximum,
        difficulty = this.difficulty,
        author = this.author
    ).also { copied ->
        copied.creationDate = this.creationDate
    }

    override fun equals(other: Any?): Boolean {
        if (other !is LabWork) return false

        return (
                this.name == other.name &&
                this.coordinates == other.coordinates &&
                this.minimalPoint == other.minimalPoint &&
                this.maximumPoint == other.maximumPoint &&
                this.personalQualitiesMaximum == other.personalQualitiesMaximum &&
                this.difficulty == other.difficulty &&
                this.author == other.author
               )
    }
}
