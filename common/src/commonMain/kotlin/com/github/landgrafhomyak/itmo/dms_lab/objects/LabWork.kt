package com.github.landgrafhomyak.itmo.dms_lab.objects

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class LabWork(
    val name: String, // Строка не может быть пустой
    val coordinates: Coordinates,
    val minimalPoint: Long, // Значение поля должно быть больше 0
    val maximumPoint: Double, // Значение поля должно быть больше 0
    val personalQualitiesMaximum: Int, // Значение поля должно быть больше 0
    val difficulty: Difficulty,
    val author: Person
) {
    var creationDate: Instant = Clock.System.now()
    var id: LabWorkId? = null
        internal set
}
