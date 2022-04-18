package com.github.landgrafhomyak.itmo.dms_lab.objects

/**
 * Сложность выполнения [лабораторной работы][LabWork]
 */
@Suppress("unused")
enum class Difficulty {
    /**
     * Сложно
     */
    HARD {
        override val id: String
            get() = "HARD"
    },

    /**
     * Безумно сложно
     */
    INSANE {
        override val id: String
            get() = "INSANE"
    },

    /**
     * Ужасно сложно
     */
    TERRIBLE {
        override val id: String
            get() = "TERRIBLE"
    };

    /**
     * Идентификатор значения
     */
    abstract val id: String

    @Suppress("RemoveRedundantQualifierName")
    companion object : EnumMap<Difficulty, String>(Difficulty.values(), { this.id })
}
