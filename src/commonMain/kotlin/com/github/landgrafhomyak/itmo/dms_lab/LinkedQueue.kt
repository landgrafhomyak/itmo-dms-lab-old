package com.github.landgrafhomyak.itmo.dms_lab

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline

/**
 * Очередь ([FIFO](https://ru.wikipedia.org/wiki/FIFO)) на односвязном списке.
 * Потоконебезопасная. Синхронизации для корутин нет.
 * ```
 *       oldest                               newest
 *     (first out)                          (last out)
 *          |                                   |
 *          v                                   v
 *    _____________     _____________     _____________
 *   | value: T    |   | value: T    |   | value: T    |
 *   |       newer +-->|       newer +-->|       newer +--> null
 *   |_____________|   |_____________|   |_____________|
 * ```
 * @param T тип элемента хранящегося в очереди
 * @property oldest первый добавленный элемент (будет возвращён первым)
 * @property newest последний добавленный элемент (будет возвращён последним)
 * @see buildLinkedQueue
 */
@Suppress("SpellCheckingInspection")
public class LinkedQueue<T> : Iterable<T> {
    /**
     * Узел [очереди][LinkedQueue]
     * @property value значение в узле
     * @property newer указатель на следующий узел (более новый, будет возвращён позже)
     * @see LinkedQueue
     */
    private data class Node<T>(
        val value: T,
        var newer: Node<T>? = null
    )

    private var newest: Node<T>? = null
    private var oldest: Node<T>? = null

    /**
     * @return `true` если [очередь][LinkedQueue] пустая, иначе `false`
     */
    public fun isEmpty(): Boolean = this.newest == null

    /**
     * @return `true` если [очередь][LinkedQueue] не пустая, иначе `false`
     */
    public fun isNotEmpty(): Boolean = this.newest != null

    /**
     * Удаляет все элементы из [очереди][LinkedQueue].
     * Сложность - `O(1)`.
     * ```
     *       oldest                               newest
     *          |                                   |
     *          x                                   x
     *
     *    _____________     _____________     _____________
     *   | value: T    |   | value: T    |   | value: T    |
     *   |       newer +-->|       newer +-->|       newer +--> null
     *   |_____________|   |_____________|   |_____________|
     * ```
     */
    @Suppress("unused")
    public fun clear() {
        this.newest = null
        this.oldest = null
    }

    /**
     * Добавляет элемент в [очередь][LinkedQueue].
     * Сложность - `O(1)`.
     * ```
     *       oldest             newest *-------------\
     *          |                  |                 |
     *          |                  x                 |
     *          v                                    v
     *    _____________     _____________      _________________
     *   | value: T    |   | value: T    |    | new_value: T    |
     *   |       newer +-->|       newer | *->|           newer +--> null
     *   |_____________|   |_____________|    |_________________|
     * ```
     */
    public fun push(value: T) {
        this.newest = Node(value)
            .apply node@{ this@LinkedQueue.newest?.newer = this@node }
        if (this.oldest == null) this.oldest = this.newest
    }

    /**
     * Возвращает самый старый элемент или кидает [NoSuchElementException], если [очередь][LinkedQueue] пуста
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun assertOldest(): Node<T> = this.oldest ?: throw NoSuchElementException("Queue is empty")

    /**
     * Вырезает узел из цепочки.
     * Сложность - `O(1)`.
     *
     * * Вырезание самого старого узла (будет возвращён первым)
     * ```
     *       oldest *-------------\              newest
     *          |                 |                 |
     *          x                 |                 |
     *                            v                 v
     *    _____________     _____________     _____________
     *   | value: T    |   | value: T    |   | value: T    |
     *   |       newer +-x |       newer +-->|       newer +--> null
     *   |_____________|   |_____________|   |_____________|
     * ```
     *
     * * Вырезание узла из середины очереди (ломает очередь, дальнейшее поведение неопределено)
     * ```
     *       oldest                                newest
     *          |                                    |
     *          |                                    |
     *          v                                    v
     *    _____________     _____________      _____________
     *   | value: T    |   | value: T    |    | value: T    |
     *   |       newer +-->|       newer +-x  |       newer +--> null
     *   |_____________|   |_____________|    |_____________|
     *                    (разрыв очереди)
     * ```
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun Node<T>.exclude(): T {
        this@LinkedQueue.oldest = this@exclude.newer
        if (this@LinkedQueue.oldest == null) this@LinkedQueue.newest = null
        return this@exclude.value
    }

    /**
     * Возвращает самый старый элемент или `null`, если очередь пуста.
     * Сложность - `O(1)`.
     */
    @Suppress("unused")
    public fun getOrNull(): T? = this.oldest?.value


    /**
     * Возвращает самый старый элемент или кидает [NoSuchElementException], если очередь пуста.
     * Сложность - `O(1)`.
     */
    @Suppress("unused")
    public fun get(): T = this.assertOldest().value

    /**
     * Удаляет и возвращает самый старый элемент или `null`, если очередь пуста.
     * Сложность - `O(1)`.
     */
    @Suppress("unused")
    public fun popOrNull(): T? = this.oldest?.exclude()

    /**
     * Удаляет и возвращает самый старый элемент или кидает [NoSuchElementException], если очередь пуста.
     * Сложность - `O(1)`.
     */
    public fun pop(): T = this.assertOldest().exclude()

    /**
     * Перебирает элементы в [очереди][LinkedQueue] от самого старого (будет возвращён первым) к самому новому (будет
     * возвращён последним), в том порядке, в котором они будут возвращены из [очереди][LinkedQueue].
     * Сложность - `O(n)`.
     * Работает быстрее чем [LinkedQueue.MutableIteratorImpl].
     * Если очередь была изменена во время итерации, поведение итератора не определено.
     *
     * * Начаьное положение указателей
     * ```
     *       pointer
     *          |
     *          v
     *    _____________     _____________     _____________
     *   | value: T    |   | value: T    |   | value: T    |
     *   |       newer +-->|       newer +-->|       newer +--> null
     *   |_____________|   |_____________|   |_____________|
     * ```
     *
     * * Перемещение указателя после [получения][Iterator.next] элемента
     * ```
     *       pointer *------------\
     *          |                 |
     *          x                 |
     *                            v
     *    _____________     _____________     _____________
     *   | value: T    |   | value: T    |   | value: T    |
     *   |       newer +-->|       newer +-->|       newer +--> null
     *   |_____________|   |_____________|   |_____________|
     * ```
     *
     * * Перемещение указателя после [получения][Iterator.next] самого позднего
     *   (будет возвращён последнем) элемента
     * ```
     *                                            pointer *------\
     *                                               |           |
     *                                               x           |
     *                                                           |
     *    _____________     _____________     _____________      |
     *   | value: T    |   | value: T    |   | value: T    |     v
     *   |       newer +-->|       newer +-->|       newer +--> null
     *   |_____________|   |_____________|   |_____________|
     * ```
     * @property pointer указатель на узел, значение которого будет возвращено следующим,
     * итерация окончена если он равен `null`
     * @see LinkedQueue.MutableIteratorImpl
     */
    private class IteratorImpl<T>(private var pointer: Node<T>?) : Iterator<T> {
        override fun hasNext(): Boolean = this.pointer != null

        override fun next(): T = (this.pointer ?: throw IllegalStateException("Queue iteration has been ended"))
            .apply node@{ this@IteratorImpl.pointer = this@node.newer }
            .value
    }

    override fun iterator(): Iterator<T> = IteratorImpl(this.oldest)

    /**
     * Перебирает элементы в [очереди][LinkedQueue] от самого старого (будет возвращён первым) к самому новому (будет
     * возвращён последним), в том порядке, в котором они будут возвращены из [очереди][LinkedQueue].
     * Может [удалять][MutableIterator.remove] последний возвращённый элемент функцией [next][Iterator.next].
     * Сложность - `O(n)`.
     * Если [очередь][LinkedQueue] была изменена во время итерации, поведение итератора не определено.
     *
     * * Начальные положения указателей
     * ```
     *    older2                     pointer
     *       |                          |
     *       v                          v
     *    _______     _______     _____________     _____________     _____________
     *   |       |   |       |   | value: T    |   | value: T    |   | value: T    |
     *   | newer +-->| newer +-->|       newer +-->|       newer +-->|       newer +--> null
     *   |_______|   |_______|   |_____________|   |_____________|   |_____________|
     *       (пустые узлы)
     * (аналог тройного указателя)
     * ```
     *
     * * Перемещение указателей после [получения элемента][Iterator.next]
     * ```
     *    older2 *-------\           pointer *------------\
     *       |           |              |                 |
     *       x           |              x                 |
     *                   v                                v
     *    _______     _______     _____________     _____________     _____________
     *   |       |   |       |   | value: T    |   | value: T    |   | value: T    |
     *   | newer +-->| newer +-->|       newer +-->|       newer +-->|       newer +--> null
     *   |_______|   |_______|   |_____________|   |_____________|   |_____________|
     * ```
     *
     * * Удаление элемента (в данном случае будет [IllegalStateException] потому что удаление
     *   производится до того как был [получен][Iterator.next] элемент)
     * ```
     *   older2                          pointer
     *      |                               |
     *      v          _______              v
     *   _______      |       |       _____________     _____________     _____________
     *  |       |     | newer +-x    | value: T    |   | value: T    |   | value: T    |
     *  | newer +-\-x |_______|   /->|       newer +-->|       newer +-->|       newer +--> null
     *  |_______| |               |  |_____________|   |_____________|   |_____________|
     *            \---------------/
     * ```
     *
     * * Перемещение указателей после [получения][Iterator.next] элемента слудующего за
     *   [удалённым][MutableIterator.remove]
     * ```
     *   older2                          pointer *------------\
     *      |                               |                 |
     *      |                               x                 |
     *      v          _______                                v
     *   _______      |       |       _____________     _____________     _____________
     *  |       |     | newer +      | value: T    |   | value: T    |   | value: T    |
     *  | newer +-\   |_______|   /->|       newer +-->|       newer +-->|       newer +--> null
     *  |_______| |               |  |_____________|   |_____________|   |_____________|
     *            \---------------/
     * ```
     *
     * * [Удаление][MutableIterator.remove] элемента следующего за [удалённым][MutableIterator.remove]
     *   до этого
     * ```
     *   older2                                            pointer
     *      |                                                 |
     *      |                                                 |
     *      v         _______        _____________            v
     *   _______     |       |      | value: T    |       _____________     _____________
     *  |       |    | newer +      |       newer +-x    | value: T    |   | value: T    |
     *  | newer +-\  |_______|  /-x |_____________|   /->|       newer +-->|       newer +--> null
     *  |_______| |             |                     |  |_____________|   |_____________|
     *            \-----------------------------------/
     * ```
     *
     * * Перемещение указателей после [получения][Iterator.next] элемента слудующего за
     *   двумя [удалённым][MutableIterator.remove]
     * ```
     *    older2                                            pointer *----------\
     *       |                                                 |               |
     *       |                                                 x               |
     *       v         _______     _____________                               v
     *    _______     |       |   | value: T    |      _____________     _____________
     *   |       |    | newer +   |       newer +     | value: T    |   | value: T    |
     *   | newer +-\  |_______|   |_____________|  /->|       newer +-->|       newer +--> null
     *   |_______| |                               |  |_____________|   |_____________|
     *             \-------------------------------/
     * ```
     *
     * * Перемещение указателей после ещё одного [получения][Iterator.next] элемента слудующего за
     *   двумя [удалённым][MutableIterator.remove]
     * ```
     *    older2 *-------------------------------------------\              pointer *-------\
     *       |                                               |                 |            |
     *       x                                               |                 x            |
     *                 _______     _____________             v                              |
     *    _______     |       |   | value: T    |      _____________     _____________      |
     *   |       |    | newer +   |       newer +     | value: T    |   | value: T    |     v
     *   | newer +-\  |_______|   |_____________|  /->|       newer +-->|       newer +--> null
     *   |_______| |                               |  |_____________|   |_____________|
     *             \-------------------------------/
     * ```
     *
     * * [Удаление][MutableIterator.remove] самого позднего (добавлен последним) элемента в
     *   [очереди][LinkedQueue]
     * ```
     *                                                     older2                        pointer
     *                                                       |                              |
     *                                                       |                              |
     *                _______     _____________              v            _____________     |
     *   _______     |       |   | value: T    |      _____________      | value: T    |    |
     *  |       |    | newer +   |       newer +     | value: T    |     |       newer +-x  v
     *  | newer +-\  |_______|   |_____________|  /->|       newer +-\-x |_____________|   null
     *  |_______| |                               |  |_____________| |                      ^
     *            \-------------------------------/                  |                      |
     *                                                               \----------------------/
     * ```
     * @property pointer указатель на узел, значение которого будет возвращено следующим,
     * если равен `null` итерация завершена
     * @property older2 тройной указатель для удаления узлов, не может быть `null`,
     * более новый элемент не может быть `null`
     * @see LinkedQueue.IteratorImpl
     * @see LinkedQueue.asMutableIterable
     */
    private inner class MutableIteratorImpl(private var pointer: Node<T>?) : MutableIterator<T> {
        @Suppress("UNCHECKED_CAST")
        private var older2: Node<T?> = Node(null, Node(null, this.pointer as Node<T?>))

        override fun hasNext(): Boolean = this.pointer != null

        override fun next(): T = (this.pointer ?: throw IllegalStateException("Queue iteration has been ended"))
            .apply node@{ this@MutableIteratorImpl.pointer = this@node.newer }
            .apply node@{
                if (this@MutableIteratorImpl.older2.newer != this@node)
                    this@MutableIteratorImpl.older2 = this@MutableIteratorImpl.older2.newer ?: throw RuntimeException("Queue was corrupted")
            }
            .value

        override fun remove() {
            if (this.older2.newer == this.pointer) throw IllegalStateException("Removing element that not in queue")
            val removedNode = this.older2.newer ?: throw RuntimeException("Queue was corrupted")
            @Suppress("UNCHECKED_CAST")
            this.older2.newer = this.pointer as Node<T?>?
            @Suppress("UNCHECKED_CAST")
            if (this@LinkedQueue.oldest == removedNode) this@LinkedQueue.oldest = removedNode.newer as Node<T>?
            if (this@LinkedQueue.newest == removedNode) {
                @Suppress("UNCHECKED_CAST")
                if (this@LinkedQueue.oldest == null) this@LinkedQueue.newest = null
                else this@LinkedQueue.newest = this.older2 as Node<T>?
            }
        }
    }

    /**
     * Предоставляет доступ к [изменяемому итератору][MutableIterator] для [очереди][LinkedQueue]
     * @property original оригинальная очередь
     * @see LinkedQueue.asMutableIterable
     * @see LinkedQueue.MutableIteratorImpl
     */
    @JvmInline
    private value class MutableIteratorDelegate<T>(private val original: LinkedQueue<T>) : MutableIterable<T> {
        override fun iterator(): MutableIterator<T> =
            this.original.MutableIteratorImpl(this.original.oldest)
    }

    /**
     * Позволяет оптимизировать итерацию и вынести менее используемый, но более медленный
     * [изменяемый итератор][MutableIterator] в [отдельный класс][MutableIteratorImpl]
     * с сохранением наследования от [MutableIterable]
     */
    @Suppress("unused")
    public fun asMutableIterable(): MutableIterable<T> = MutableIteratorDelegate(this)
}

/**
 * Строит [очередь][LinkedQueue]
 * @see LinkedQueue
 */
@OptIn(ExperimentalContracts::class)
@Suppress("unused")
public inline fun <T> buildLinkedQueue(builder: LinkedQueue<T>.() -> Unit): LinkedQueue<T> {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return LinkedQueue<T>().apply(builder)
}