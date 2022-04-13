package com.github.landgrafhomyak.itmo.dms_lab.interactive.console

import com.github.landgrafhomyak.itmo.dms_lab.collections.ArrayStack
import com.github.landgrafhomyak.itmo.dms_lab.collections.RedBlackTreeMap
import com.github.landgrafhomyak.itmo.dms_lab.collections.Stack
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmStatic

class ObjectParser private constructor(
    private val raw: String
) {

    companion object {
        @JvmStatic
        fun parse(raw: String) = ObjectParser(raw).out
    }

    sealed interface Value {
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

    private enum class State {
        KEY,
        VALUE,
        SLASH
    }

    private data class ObjectParserStackNode(
        val values: MutableMap<String, Value>,
    ) {
        var lastKey: String? = null
    }

    private class IntRangeWithExclude(
        exclude: Iterable<Int>,
        from: Int,
        private val until: Int
    ) : Iterable<Int>, Iterator<Int> {
        private var pos = from
        private val excludeIterator = exclude.iterator()
        private var nextExclude: Int = -1

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

    private fun wrap(map: Map<String, Value>) = Value.Compose(map)
    private fun wrap(string: String) = Value.Simple(string)


    private var state: State = State.KEY
    private var startPos = 0
    private val excludeIndexes = mutableListOf<Int>()
    private val stack: Stack<ObjectParserStackNode> = ArrayStack()
    private var inString = false
    private val out: Map<String, Value>

    private inline fun pushString(pos: Int) {
        val node = this.stack.top
        this.stack.top.values[node.lastKey!!.also { node.lastKey = null }] = this.wrap(
            raw.slice(IntRangeWithExclude(excludeIndexes, startPos, pos))
        )
    }

    private inline fun popStack(pos: Int) {
        val values = this.stack.pop().values
        if (this.stack.isEmpty())
            throw ParseError(pos, "Непарная закрывающая скобка")

        this.stack.top.values[this.stack.top.lastKey!!] = this.wrap(values)
        this.stack.top.lastKey = null

        this.state = State.KEY
        this.startPos = pos + 1
    }

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
                        this.popStack(pos)
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
                            this.pushString(pos)
                            this.state = State.KEY
                            this.startPos = pos + 1
                        }
                    }

                    c == '}'         -> if (!this.inString) {
                        this.pushString(pos)
                        this.popStack(pos)
                        this.state = State.KEY
                        this.startPos = pos + 1
                    }

                    c == '{'         -> if (!this.inString) {
                        if (pos != this.startPos)
                            this.pushString(pos)
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
            this.pushString(this.raw.length)
        }

        if (this.inString)
            throw ParseError(this.raw.length - 1, "Незакрытый строковый литерал")
        if (this.stack.top.lastKey != null || (this.state == State.KEY && this.startPos != this.raw.length)) {
            throw ParseError(this.raw.length - 1, "Последнему ключу не присвоено значение")
        }
        this.out = this.stack.popOrNull()?.values ?: throw ParseError(this.raw.length - 1, "Незакрытый объект")
        if (this.stack.isNotEmpty())
            throw ParseError(this.raw.length - 1, "Незакрытый объект")

    }

}

