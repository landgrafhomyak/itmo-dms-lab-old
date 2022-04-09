package com.github.landgrafhomyak.itmo.dms_lab.objects

data class Person(
    val name: String,  // Строка не может быть пустой
    val weight: Float, // Значение поля должно быть больше 0
    val eyeColor: EyeColor,
    val hairColor: HairColor,
    val nationality: Country,
)