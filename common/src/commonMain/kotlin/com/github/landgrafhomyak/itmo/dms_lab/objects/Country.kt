package com.github.landgrafhomyak.itmo.dms_lab.objects


/**
 * Страна/национальность [автора лабораторной работы][Person]
 */
@Suppress("unused")
enum class Country {
    /**
     * Россия
     */
    RUSSIA {
        override val id: String
            get() = "RUSSIA"
    },

    /**
     * Германия
     */
    GERMANY {
        override val id: String
            get() = "GERMANY"
    },

    /**
     * Китай
     */
    CHINA {
        override val id: String
            get() = "CHINA"
    },

    /**
     * Северная Корея
     */
    NORTH_KOREA {
        override val id: String
            get() = "NORTH_KOREA"
    },

    /**
     * Япония
     */
    JAPAN {
        override val id: String
            get() = "JAPAN"
    };
    /**
     * Идентификатор значения
     */
    abstract val id: String

    @Suppress("RemoveRedundantQualifierName")
    companion object : EnumMap<Country, String>(Country.values(), { this.id })
}