package io.github.landgrafhomyak.itmo.dms_lab.interop

import kotlinx.serialization.SerializationException

/**
 * Состояние парсера разбора выражения составного объекта данных
 */
@Suppress("SpellCheckingInspection")
public class ObjectParser private constructor(
    private val raw: String
) {
    /**
     * Итератор для извлечения срезов из строки, исключающий из [обычного отрезка][IntRange] значения множества.
     * Наследуется от [Iterable] для передачи в [String.slice] без создания дополнительного объекта, является одноразовым
     * @param exclude последовательность чисел для исключения, должна быть строго возрастающей
     * @param from начальное значение отрезка
     * @param until конечное значение отрезка (включительно)
     */
    private class IntRangeWithExclude(
        exclude: Iterable<Int>,
        from: Int,
        private val until: Int
    ) : Iterable<Int>, Iterator<Int> {
        init {
            if (from > this.until) {
                throw IllegalStateException("Начальное отрезка значение должно быть не больше конечного")
            }
        }

        /**
         * Текущая позиция итератора
         */
        private var pos = from

        /**
         * Итаратор последовательности с исключёнными индексами
         */
        private val excludeIterator = exclude.iterator()

        /**
         * Следующее исключённое число
         */
        private var nextExclude: Int = from - 1

        init {
            for (e in this.excludeIterator) {
                this.nextExclude = e
                if (from <= e) {
                    break
                }
            }
        }

        override fun iterator(): Iterator<Int> = this

        override fun hasNext(): Boolean {
            while (this.pos <= this.until) {
                if (this.pos != this.nextExclude) {
                    return true
                }
                this.pos++
                if (this.excludeIterator.hasNext()) {
                    this.nextExclude = this.excludeIterator.next()
                }
            }
            return false
        }

        override fun next(): Int {
            if (!this.hasNext())
                throw IllegalStateException("Все индексы были перебраны")
            return this.pos++
        }
    }

    private sealed class Entity {
        data class Pair(val key: String, val value: String?) : Entity()

        object ObjectOpen : Entity()
        object ObjectClose : Entity()
    }

    private var lastEntity: Entity? = null

    private var pos: Int = 0

    private fun nextEntry(): Entity {
        this.lastEntity?.apply le@{ return@nextEntry this@le }

        spaces@ while (this.pos < this.raw.length) {
            val c = this.raw[this.pos]
            when {
                c.isWhitespace() -> {}
                else             -> break@spaces
            }
            this.pos++
        }
        if (this.raw[this.pos] == '{') {
            this.lastEntity = Entity.ObjectOpen
            return Entity.ObjectOpen
        }

        val keyStartPos = this.pos
        key@ while (this.pos < this.raw.length) {
            val c = this.raw[this.pos]
            when {
                c.isWhitespace() -> break@key
                c.isDigit()      -> {}
                c.isLetter()     -> {}
                c == '_'         -> {}
                c == '}'         -> break@key
                else             -> throw SerializationException("Неправильный символ в ключе")
            }
            this.pos++
        }
        val key = this.raw.substring(keyStartPos until this.pos)

        spaces@ while (this.pos < this.raw.length) {
            val c = this.raw[this.pos]
            when {
                c.isWhitespace() -> {}
                else             -> break@spaces
            }
            this.pos++
        }
        when (this.raw[this.pos]) {
            '}'  ->
                if (key.isEmpty()) {
                    this.lastEntity = Entity.ObjectClose
                    return Entity.ObjectClose
                } else
                    throw SerializationException("Ключ не может быть без значения или содержать в себе скобки")
            '='  -> this.pos++
            else -> throw SerializationException("Невалидный переход от ключа к значению")
        }

        spaces@ while (this.pos < this.raw.length) {
            val c = this.raw[this.pos]
            when {
                c.isWhitespace() -> {}
                else             -> break@spaces
            }
            this.pos++
        }

        val valueStartPos = this.pos
        var expectingSlash = false
        var inString = false
        val excludedIndicies = mutableListOf<Int>()

        value@ while (this.pos < this.raw.length) {
            val c = this.raw[this.pos++]
            if (expectingSlash) {
                when (c) {
                    '"', '\\' -> {
                        expectingSlash = false
                    }
                    else      -> throw SerializationException("Невалидный специальный символ, разрешены только '\"' и '\\'")
                }
            } else {
                when {
                    c == '"'         -> {
                        excludedIndicies.add(this.pos)
                        inString = !inString
                    }

                    c == '\\'        -> if (inString) {
                        excludedIndicies.add(this.pos)
                        expectingSlash = true
                    }

                    c.isWhitespace() -> if (!inString) {
                        break@value
                    }

                    c == '}'         -> if (!inString) {
                        this.pos--
                        break@value
                    }

                    c == '{'         -> if (valueStartPos == this.pos) {
                        this.pos--
                        break@value
                    }
                }

            }
        }

        val value: String? =
            if (this.pos == valueStartPos && this.raw[this.pos] == '{') null
            else this.raw.slice(IntRangeWithExclude(excludedIndicies, valueStartPos, this.pos))

        return Entity.Pair(key, value)
    }
}

