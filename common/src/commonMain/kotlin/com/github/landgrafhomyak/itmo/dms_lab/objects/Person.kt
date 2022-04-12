package com.github.landgrafhomyak.itmo.dms_lab.objects

/**
 * Автор [лабораторной работы][LabWork]
 * @property name имя автора ( `isNotBlank` )
 * @property weight вес ( `> 0` )
 * @property eyeColor [цвет глаз][EyeColor]
 * @property hairColor [цвет волос][HairColor]
 * @property nationality [национальность][Country]
 */
@Suppress("KDocUnresolvedReference", "unused")
data class Person(
    val name: String,
    val weight: Float, // Значение поля должно быть больше 0
    val eyeColor: EyeColor,
    val hairColor: HairColor,
    val nationality: Country,
) {
    init {
        if (this.name.isBlank()) throw IllegalArgumentException("Имя автора должно быть непустым")
        if (this.weight <= 0) throw IllegalArgumentException("Вес автора должен быть строго положительным")
    }
}