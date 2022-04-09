package com.github.landgrafhomyak.itmo.dms_lab.objects

import kotlinx.datetime.Instant

data class LabWork(
    var id: Long, // Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    var name: String, // Строка не может быть пустой
    var coordinates: Coordinates,
    var creationDate: Instant, // Значение этого поля должно генерироваться автоматически
    var minimalPoint: Long, // Значение поля должно быть больше 0
    var maximumPoint: Double, // Значение поля должно быть больше 0
    var personalQualitiesMaximum: Int, // Значение поля должно быть больше 0
    var difficulty: Difficulty,
    var author: Person
)
