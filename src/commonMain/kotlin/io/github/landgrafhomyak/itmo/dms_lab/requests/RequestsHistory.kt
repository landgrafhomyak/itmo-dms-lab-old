package io.github.landgrafhomyak.itmo.dms_lab.requests

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * История выполнения [запросов][BoundRequest].
 * Гарантирует хранение не более [capacity] [запросов][BoundRequest].
 *
 * ```
 *       ___ ___ ___ ___ ___ ___
 *   /->| 0 | 1 | 2 | 3 | 4 | 5 +--\
 *   |  |___|___|___|___|___|___|  |
 *   |                             |
 *   \-----------------------------/
 * ```
 * @param R тип запроса
 * @param capacity максимальный размер истории
 * @see RequestsHistory.IteratorImpl
 */
public class RequestsHistory<R : BoundRequest<*, *>>(capacity: UInt) : Iterable<R> {
    init {
        require(capacity <= Int.MAX_VALUE.toUInt()) { "Array size out of bounds" }
    }

    @Suppress("UNCHECKED_CAST", "GrazieInspection")
    private val storage: Array<Any?> = arrayOfNulls<Any?>(capacity.toInt()) // arrayOfNulls required reified type

    /**
     * Позиция на которую будет записан следующий запрос
     */
    private var nextPos = 0u

    /**
     * Максимальный размер [истории][RequestsHistory]
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public val capacity: UInt
        get() = this.storage.size.toUInt()

    /**
     * Циклическая инкрементация, гарантирующая, что индекс всегда является валидным
     * для получения [запроса][BoundRequest]
     */
    @Suppress("NOTHING_TO_INLINE", "SpellCheckingInspection")
    private inline fun UInt.next(): UInt = (this@next + 1u) % this@RequestsHistory.capacity

    @Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")
    private inline operator fun get(index: UInt): R? = this.storage[index.toInt()] as R?

    @Suppress("NOTHING_TO_INLINE")
    private inline operator fun set(index: UInt, value: R) {
        this.storage[index.toInt()] = value
    }

    /**
     * Добавляет [запрос][BoundRequest] в [историю][RequestsHistory]
     */
    @Suppress("unused")
    public fun push(request: R) {
        if (this.capacity == 0u) return
        this[this.nextPos] = request
        this.nextPos = this.nextPos.next()
    }

    /**
     * Перебирает [запросы][BoundRequest] в [истории][RequestsHistory].
     * Сложность - O(n).
     * Если [история][RequestsHistory] изменена во время итерации - поведение не определено.
     *
     * * Начальное положение указателей
     * ```
     *    @nextPos pos
     *           | |    anyReturned = false
     *           v v
     *   ___ ___ ___ ___
     *  | 0 | 1 | 2 | 3 |
     *  |___|___|___|___|
     * ```
     *
     * * Перемещение указателей после [получения][Iterator.next] [запроса][BoundRequest]
     * ```
     *    @nextPos pos *
     *           | |   |   anyReturned = false
     *           | x   |
     *           v     v
     *   ___ ___ ___ ___
     *  | 0 | 1 | 2 | 3 |
     *  |___|___|___|___|
     * ```
     *
     * * Перемещение указателей через границу [истории][RequestsHistory]
     * ```
     *    /--------* pos
     *    | @nextPos  |   anyReturned = true
     *    |       |   x
     *    v       v
     *   ___ ___ ___ ___
     *  | 0 | 1 | 2 | 3 |
     *  |___|___|___|___|
     * ```
     *
     *
     * * Конечное положение указателей
     * ```
     *         pos @nextPos
     *           | |    anyReturned = false
     *           v v
     *   ___ ___ ___ ___
     *  | 0 | 1 | 2 | 3 |
     *  |___|___|___|___|
     * ```
     * @property pos указатель на [запрос][BoundRequest] который будет возвращён следующим
     * @property anyReturned флаг показывающий, что итератор вернул как минимум один [запрос][BoundRequest]
     * @property nextPos указатель на начало и позицию, идущую за последним [запросом][BoundRequest]
     * в [истории][RequestsHistory]
     * @see RequestsHistory.EmptyIteratorImpl
     */
    private inner class IteratorImpl(startPos: UInt) : Iterator<R> {
        private var pos = startPos
        private var anyReturned = false

        override fun hasNext(): Boolean = !this.anyReturned || this.pos != this@RequestsHistory.nextPos

        override fun next(): R {
            if (!this.hasNext()) throw IllegalStateException("History iteration has been ended")
            this.anyReturned = true
            return (this@RequestsHistory[this.pos] ?: throw IllegalStateException("History was corrupted"))
                .also { this.pos = this.pos.next() }
        }
    }

    /**
     * Итератор-заглушка для пустой [истории][RequestsHistory]. Не возвращает [запросы][BoundRequest]
     * Сложность - O(1).
     * Если [история][RequestsHistory] изменена во время итерации - поведение не определено.
     * @see RequestsHistory.IteratorImpl
     */
    private object EmptyIteratorImpl : Iterator<BoundRequest<*, *>> {
        override fun hasNext(): Boolean = false

        override fun next(): BoundRequest<*, *> = throw IllegalStateException("Empty history hasn't requests")
    }

    override fun iterator(): Iterator<R> {
        @Suppress("UNCHECKED_CAST")
        if (this.capacity == 0u) return EmptyIteratorImpl as Iterator<R>
        @Suppress("UNCHECKED_CAST")
        if (this[0u] == null) return EmptyIteratorImpl as Iterator<R>
        if (this[this.capacity - 1u] == null) return this.IteratorImpl(0u)
        return this.IteratorImpl(this.nextPos)
    }
}


/**
 * Собирает [историю][RequestsHistory]
 */
@OptIn(ExperimentalContracts::class)
@Suppress("unused")
public inline fun <R : BoundRequest<*, *>> buildRequestsHistory(capacity: UInt, builder: RequestsHistory<R>.() -> Unit): RequestsHistory<R> {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return RequestsHistory<R>(capacity).apply(builder)
}