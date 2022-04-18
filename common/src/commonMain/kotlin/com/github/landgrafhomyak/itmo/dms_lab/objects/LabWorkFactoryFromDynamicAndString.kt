package com.github.landgrafhomyak.itmo.dms_lab.objects

import com.github.landgrafhomyak.itmo.dms_lab.Factory
import kotlin.jvm.JvmInline

/**
 * Собирает объект [лабораторной работы][LabWork] из словаря со строками
 */
class LabWorkFactoryFromDynamicAndString(map: Map<String, Value>) : Factory<LabWork> {
    /**
     * Позволяет хранить в одном словаре объекты разных типов без нарушения типизации
     * @see LabWorkFactoryFromDynamicAndString.Value.Simple
     * @see LabWorkFactoryFromDynamicAndString.Value.Compose
     */
    sealed interface Value {
        /**
         * Класс-обёртка для хранения составного объекта
         * @property original настоящий объект словаря
         */
        @JvmInline
        value class Compose(
            @Suppress("MemberVisibilityCanBePrivate")
            val original: Map<String, Value>
        ) : Map<String, Value>, Value {
            override val entries: Set<Map.Entry<String, Value>>
                get() = this.original.entries
            override val keys: Set<String>
                get() = this.original.keys
            override val size: Int
                get() = this.original.size
            override val values: Collection<Value>
                get() = this.original.values

            override fun containsKey(key: String): Boolean = this.original.containsKey(key)
            override fun containsValue(value: Value): Boolean = this.original.containsValue(value)
            override operator fun get(key: String): Value? = this.original[key]
            override fun isEmpty(): Boolean = this.original.isEmpty()
            override fun toString(): String = this.original.toString()
        }

        /**
         * Класс-обёртка для хранения примитивных строковых данных
         * @property original настоящий объект строки
         */
        @JvmInline
        value class Simple(
            @Suppress("MemberVisibilityCanBePrivate")
            val original: String
        ) : CharSequence, Value {
            override val length: Int
                get() = this.original.length

            override fun get(index: Int): Char = this.original[index]
            override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = this.original.subSequence(startIndex, endIndex)
            override fun toString(): String = this.original
        }
    }

    private val compiled: LabWork
    override fun build(): LabWork = this.compiled.copy()

    companion object {
        /**
         * Сокращение для получения примитивных данных из [Map]&lt;[String], [LabWorkFactoryFromDynamicAndString.Value]&gt;
         */
        // @JvmStatic
        @Suppress("NOTHING_TO_INLINE")
        private inline fun getString(map: Map<String, Value>, key: String, message: String): String =
            (map[key] as? Value.Simple)?.original ?: throw FailedCreateObjectException(message)

        /**
         * Сокращение для получения составного объекта из [Map]&lt;[String], [LabWorkFactoryFromDynamicAndString.Value]&gt;
         */
        // @JvmStatic
        @Suppress("NOTHING_TO_INLINE")
        private inline fun getObject(map: Map<String, Value>, key: String, message: String): Map<String, Value> =
            (map[key] as? Value.Compose)?.original ?: throw FailedCreateObjectException(message)

        /**
         * Парсит значение [Difficulty] из строки
         */
        // @JvmStatic
        @Suppress("NOTHING_TO_INLINE")
        inline fun parseDifficulty(raw: String): Difficulty = Difficulty[raw] ?: throw FailedCreateObjectException("Неизвестная сложность")


        /**
         * Парсит значение [EyeColor] из строки
         */
        // @JvmStatic
        @Suppress("NOTHING_TO_INLINE")
        inline fun parseEyeColor(raw: String): EyeColor = EyeColor[raw] ?: throw FailedCreateObjectException("Неизвестный цвет глаз")

        /**
         * Парсит значение [HairColor] из строки
         */
        // @JvmStatic
        @Suppress("NOTHING_TO_INLINE")
        inline fun parseHairColor(raw: String): HairColor = HairColor[raw] ?: throw FailedCreateObjectException("Неизвестный цвет волос")


        /**
         * Парсит значение [Country] из строки
         */
        // @JvmStatic
        @Suppress("NOTHING_TO_INLINE")
        inline fun parseCountry(raw: String): Country = Country[raw] ?: throw FailedCreateObjectException("Неизвестная национальность")
    }

    init {

        this.compiled = LabWork(
            getString(map, "name", "Имя лабораторной работы не было получено"),
            getObject(map, "coordinates", "Координаты не были получены").let { map2 ->
                Coordinates(
                    getString(map2, "x", "Координата X не была получена")
                        .toLongOrNull() ?: throw FailedCreateObjectException("Координата X должна быть целым числом"),
                    getString(map2, "y", "Координата Y не была получена")
                        .toLongOrNull() ?: throw FailedCreateObjectException("Координата Y должна быть целым числом")
                )
            },
            getString(map, "minimal_point", "Минимальная точка не была получена")
                .toLongOrNull() ?: throw FailedCreateObjectException("Минимальная точка должна быть целым числом"),
            getString(map, "maximum_point", "Максимальная точка не была получена")
                .toDoubleOrNull() ?: throw FailedCreateObjectException("Максимальная точка должна быть вещественным числом"),
            getString(map, "personal_qualities_maximum", "Максимальный показатель личных качеств не были получена")
                .toIntOrNull() ?: throw FailedCreateObjectException("Максимальный показатель личных качеств быть целым числом"),
            parseDifficulty(getString(map, "difficulty", "Сложность лабораторной работы не была получена")),
            getObject(map, "author", "Автор не был получен").let { map2 ->
                Person(
                    getString(map2, "name", "Имя автора не было получено"),
                    getString(map2, "weight", "Вес автора")
                        .toFloatOrNull() ?: throw FailedCreateObjectException("Вес автора должен быть вещественным числом"),
                    parseEyeColor(getString(map2, "eye_color", "Цвет глаз автора не был получен")),
                    parseHairColor(getString(map2, "hair_color", "Цвет волос автора не был получен")),
                    parseCountry(getString(map2, "nationality", "Национальность автора не была получена")),
                )
            }
        )
    }

    override fun hashCode(): Int = this.compiled.hashCode()
    override fun equals(other: Any?): Boolean {
        if (other is Factory<*>)
            return other == this.compiled
        return this.compiled == other
    }
}