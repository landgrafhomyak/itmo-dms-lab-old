package com.github.landgrafhomyak.itmo.dms_lab.interactive.console

import com.github.landgrafhomyak.itmo.dms_lab.collections.ArrayStack
import com.github.landgrafhomyak.itmo.dms_lab.collections.RedBlackTreeMap
import com.github.landgrafhomyak.itmo.dms_lab.collections.Stack
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorkFactoryFromDynamicAndString
import kotlin.jvm.JvmStatic

/**
 * Состояние парсера разбора выражения составного объекта данных
 * @see ObjectParser.parse
 */
@Suppress("SpellCheckingInspection")
class ObjectParser private constructor(
    private val raw: String
) {

    companion object {
        /**
         * Разбирает выражение в переданной строке в формате
         * * `key=value` - простые строковые данные доступные по ключу `key`
         * * `key=" some spaces "joined" special \" \\ symbols "` - строковые данные с эеранированием спец. символов
         * * `obj={ key=value }` - составные объекты, содержащие в себе другие данные и объекты
         *
         * В строке может находиться любое количество выражений разделённых пробельными символами
         * @throws ParseError если нарушен синтаксис
         * @return словарь пар _строка->строка_ и _строка->словарь_
         * @see LabWorkFactoryFromDynamicAndString.Value
         */
        @JvmStatic
        fun parse(raw: String) = ObjectParser(raw).out
    }

    /**
     * Состояние парсера
     */
    private enum class State {
        /**
         * Парсит ключ
         */
        KEY,

        /**
         * Парсит значение
         */
        VALUE,

        /**
         * Ожидает значение спец. символа
         */
        SLASH
    }

    /**
     * Узел стека для обработки вложенных обектов
     * @property values словарь с данными текущего объекта
     * @property lastKey последний необработанный ключ
     */
    private data class ObjectParserStackNode(
        val values: MutableMap<String, LabWorkFactoryFromDynamicAndString.Value>,
    ) {
        var lastKey: String? = null
    }

    /**
     * Итератор для извлечения срезов из строки, исключающий из [обычного отрезка][IntRange] значения множества.
     * Наследуется от [Iterable] для передачи в [String.slice] без создания дополнительного объекта, является одноразовым
     * @param exclude последовательность чисел для исключения, должна быть строго возрастающей
     * @param from начальное значение отрезка
     * @param until конечное значение отрезка (не достигается)
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
            while (this.pos < this.until) {
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

    /**
     * Состояние итератора
     */
    private var state: State = State.KEY

    /**
     * Начальная позиция последней лексемы
     */
    private var startPos = 0

    /**
     * Исключённый индексы их последней распаршеной строки
     */
    private val excludeIndexes = mutableListOf<Int>()

    /**
     * Стек вложенных объектов
     */
    private val stack: Stack<ObjectParserStackNode> = ArrayStack()

    /**
     * Проверка, что парсер нужно экранировать все символы
     */
    private var inString = false

    /**
     * Самый высокоуровневый объект. Результат работы парсера
     */
    private val out: Map<String, LabWorkFactoryFromDynamicAndString.Value>

    /**
     * Сохраняет данные в словарь и проверят дупликацию ключей
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun saveObject(pos: Int, key: String, value: LabWorkFactoryFromDynamicAndString.Value) {
        if (this.stack.top.values.put(key, value) != null) {
            throw ParseError(pos, "Дублирование ключа $key")
        }
    }

    /**
     * Добавляет последние распаршеные примитивные данные в строку
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun saveString(pos: Int) {
        val node = this.stack.top
        this.saveObject(
            pos, node.lastKey!!.also { node.lastKey = null }, LabWorkFactoryFromDynamicAndString.Value.Simple(
                this.raw.slice(IntRangeWithExclude(this.excludeIndexes, this.startPos, pos))
            )
        )
        this.excludeIndexes.clear()
    }

    /**
     * Удаляет последний объект из [стека][ObjectParser.stack] и добавляет его как значение в предыдущий
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun popAndSaveStack(pos: Int) {
        val values = this.stack.pop().values
        if (this.stack.isEmpty())
            throw ParseError(pos, "Непарная закрывающая скобка")

        this.saveObject(pos, this.stack.top.lastKey!!, LabWorkFactoryFromDynamicAndString.Value.Compose(values))
        this.stack.top.lastKey = null

        this.state = State.KEY
        this.startPos = pos + 1
    }

    /**
     * Создаёт новый объект на стеке
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun pushStack() {
        this.stack.push(ObjectParserStackNode(RedBlackTreeMap()))
    }

    init {
        this.pushStack()

        this.raw.forEachIndexed { pos, c ->
            when (this.state) {
                State.KEY   -> when {
                    c == '='         -> {
                        if (startPos == pos) {
                            throw ParseError(pos, "Название поля не было передано")
                        }
                        this.stack.top.lastKey = raw.slice(this.startPos until pos).trim().also { key ->
                            if (key.any { c -> c.isWhitespace() })
                                throw ParseError(pos, "Название поля не должно содержать пробельные символы")
                        }
                        this.state = State.VALUE
                        this.startPos = pos + 1
                    }
                    c.isWhitespace() -> {
                        if (pos == this.startPos)
                            this.startPos++
                    }
                    c.isLetter()     -> {}
                    c.isDigit()      -> {}
                    c == '_'         -> {}
                    c == '}'         -> {
                        if (pos != startPos)
                            throw ParseError(pos, "Новый объект должен быть значением")
                        this.popAndSaveStack(pos)
                    }
                    else             -> throw ParseError(pos, "Невалидный символ в названии поля")
                }

                State.VALUE -> when {
                    c == '"'         -> {
                        this.excludeIndexes.add(pos)
                        this.inString = !this.inString
                    }

                    c == '\\'        -> if (this.inString) {
                        this.excludeIndexes.add(pos)
                        this.state = State.SLASH
                    }

                    c.isWhitespace() -> if (!this.inString) {
                        if (pos == this.startPos) {
                            this.startPos++
                        } else {
                            this.saveString(pos)
                            this.state = State.KEY
                            this.startPos = pos + 1
                        }
                    }

                    c == '}'         -> if (!this.inString) {
                        this.saveString(pos)
                        this.popAndSaveStack(pos)
                        this.state = State.KEY
                        this.startPos = pos + 1
                    }

                    c == '{'         -> if (!this.inString) {
                        if (pos != this.startPos)
                            this.saveString(pos)
                        this.pushStack()
                        this.state = State.KEY
                        this.startPos = pos + 1
                    }
                }

                State.SLASH -> when (c) {
                    '"', '\\' -> {
                        this.state = State.VALUE
                    }
                    else      -> throw ParseError(pos, "Невалидный специальный символ, разрешены только '\"' и '\\'")
                }
            }
        }

        if (this.state == State.VALUE) {
            this.saveString(this.raw.length)
        }

        if (this.inString) {
            throw ParseError(this.raw.length - 1, "Незакрытый строковый литерал")
        }
        if (this.stack.top.lastKey != null || (this.state == State.KEY && this.startPos != this.raw.length)) {
            throw ParseError(this.raw.length - 1, "Последнему ключу не присвоено значение")
        }
        this.out = this.stack.popOrNull()?.values ?: throw ParseError(this.raw.length - 1, "Незакрытый объект")
        if (this.stack.isNotEmpty()) {
            throw ParseError(this.raw.length - 1, "Незакрытый объект")
        }
    }

}

