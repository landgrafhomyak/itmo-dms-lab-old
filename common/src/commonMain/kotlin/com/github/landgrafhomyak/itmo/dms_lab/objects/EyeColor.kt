package com.github.landgrafhomyak.itmo.dms_lab.objects

/**
 * Цвет глаз [автора лабораторной работы][Person]
 */
@Suppress("unused")
enum class EyeColor {
    /**
     * Зелёный
     */
    GREEN {
        override val id: String
            get() = "GREEN"
    },

    /**
     * Чёрный
     */
    BLACK {
        override val id: String
            get() = "BLACK"
    },

    /**
     * Синий
     */
    BLUE {
        override val id: String
            get() = "BLUE"
    },

    /**
     * Жёлтый
     */
    YELLOW {
        override val id: String
            get() = "YELLOW"
    },

    /**
     * Белый
     */
    WHITE {
        override val id: String
            get() = "WHITE"
    };
    /**
     * Идентификатор значения
     */
    abstract val id: String

    @Suppress("RemoveRedundantQualifierName")
    companion object : EnumMap<EyeColor, String>(EyeColor.values(), { this.id })
}