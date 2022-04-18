package com.github.landgrafhomyak.itmo.dms_lab.objects

/**
 * Цвет волос [автора лабораторной работы][Person]
 */
@Suppress("unused")
enum class HairColor {
    /**
     * Рыжий
     */
    RED {
        override val id: String
            get() = "RED"
    },

    /**
     * Седой
     */
    WHITE {
        override val id: String
            get() = "WHITE"
    },

    /**
     * Тёмный
     */
    BROWN {
        override val id: String
            get() = "BROWN"
    };


    /**
     * Идентификатор значения
     */
    abstract val id: String

    @Suppress("RemoveRedundantQualifierName")
    companion object : EnumMap<HairColor, String>(HairColor.values(), { this.id })
}