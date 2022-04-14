package com.github.landgrafhomyak.itmo.dms_lab.objects

import com.github.landgrafhomyak.itmo.dms_lab.collections.AbstractLinkedListPriorityQueue
import com.github.landgrafhomyak.itmo.dms_lab.collections.AbstractRedBlackTree
import com.github.landgrafhomyak.itmo.dms_lab.collections.BinaryTreeLinksWithColor
import com.github.landgrafhomyak.itmo.dms_lab.collections.DoublyLinkedListLinks
import com.github.landgrafhomyak.itmo.dms_lab.collections.PriorityQueue
import com.github.landgrafhomyak.itmo.dms_lab.collections.RedBlackTreeSetWithKeyAccess
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.jvm.JvmInline

/**
 * Коллекция (множество) для хранения лабораторных работ на 11-связном списке
 */
class LabWorksCollection : Iterable<LabWork> {
    /**
     * Дата создания коллекции
     */
    var creationDate: Instant = Clock.System.now()

    /**
     * Обёртка вокруг элемента коллекции позволяющая ему быть частью нескольких связных структур
     * @property value значение узла
     */
    private class Node(
        var value: LabWork
    ) {
        /**
         * @see LabWorksCollection.byId
         */
        val byId = BinaryTreeLinksWithColor<Node, AbstractRedBlackTree.Color>()

        /**
         * @see LabWorksCollection.byMaximumPoint
         */
        val byMaximumPoint = BinaryTreeLinksWithColor<Node, AbstractRedBlackTree.Color>()

        /**
         * @see LabWorksCollection.byCoordinateXMax
         */
        val byCoordinateXMax = DoublyLinkedListLinks<Node>()

        /**
         * @see LabWorksCollection.byCoordinateXSorted
         */
        val byCoordinateXSorted = BinaryTreeLinksWithColor<Node, AbstractRedBlackTree.Color>()
    }

    /**
     * Множество всех [лабораторных работ][LabWork] с доступом по их [идентификатору][LabWork.id]
     * @see RedBlackTreeSetWithKeyAccess
     */
    @Suppress("RemoveExplicitTypeArguments")
    private val byId = AbstractRedBlackTree<Node, LabWorkId>(Node::byId) node@{ this@node.value.id!! }

    @Suppress("SpellCheckingInspection")
    /**
     * Обёртка над компаратором [вещественных чисел][Double] которая исключает дупликацию ключей и реверсирует сортировку
     * @see LabWorksCollection.byMaximumPoint
     */
    @JvmInline
    private value class NoDuplicateComparatorDouble(private val value: Double) : Comparable<NoDuplicateComparatorDouble> {
        override fun compareTo(other: NoDuplicateComparatorDouble): Int = when (this.value.compareTo(other.value)) {
            -1   -> 1
            0    -> -1
            1    -> -1
            else -> throw IllegalArgumentException("Компаратор вернул невалидное значение")
        }

    }

    /**
     * Множество всех [лабораторных работ][LabWork] отсортированное по значению [максимальной точки][LabWork.maximumPoint]
     * @see LabWorksCollection.NoDuplicateComparatorDouble
     */
    @Suppress("RemoveExplicitTypeArguments")
    private val byMaximumPoint = AbstractRedBlackTree<Node, NoDuplicateComparatorDouble>(Node::byMaximumPoint) node@{ NoDuplicateComparatorDouble(this@node.value.maximumPoint) }

    /**
     * Очередь из всех [лабораторных работ][LabWork] с приоритетом по [координате X][Coordinates.x]
     *
     * _**Todo Оптимизировать O(n) -> O(log N)**_
     * @see PriorityQueue
     */
    @Suppress("RemoveExplicitTypeArguments")
    private val byCoordinateXMax = AbstractLinkedListPriorityQueue<Node, Long>(Node::byCoordinateXMax) node@{ this@node.value.coordinates.x }

    @Suppress("SpellCheckingInspection")
    /**
     * Обёртка над компаратором [целых чисел][Long] которая исключает дупликацию ключей и реверсирует сортировку
     * @see LabWorksCollection.byCoordinateXSorted
     */
    @JvmInline
    private value class NoDuplicateComparatorLong(private val value: Long) : Comparable<NoDuplicateComparatorLong> {
        override fun compareTo(other: NoDuplicateComparatorLong): Int = when (this.value.compareTo(other.value)) {
            -1   -> 1
            0    -> -1
            1    -> -1
            else -> throw IllegalArgumentException("Компаратор вернул невалидное значение")
        }

    }

    /**
     * Множество всех [лабораторных работ][LabWork] отсортированное по значению [координаты X][Coordinates.x]
     * @see LabWorksCollection.NoDuplicateComparatorLong
     */
    @Suppress("RemoveExplicitTypeArguments")
    private val byCoordinateXSorted = AbstractRedBlackTree<Node, NoDuplicateComparatorLong>(Node::byCoordinateXSorted) node@{ NoDuplicateComparatorLong(this@node.value.coordinates.x) }

    private var nextId: LabWorkId = 1

    /**
     * Добавляет узел к коллекции
     * @return идентификатор нового узла
     */
    private fun bind(node: Node): LabWorkId {
        if (node.value.id != null)
            throw IllegalStateException("Лабораторная уже имеет владельца, добавление в коллекцию отменено")
        node.value.id = this.nextId++

        this.byId.bind(node)?.also { old ->
            this.untie(old, false)
        }
        this.byMaximumPoint.bind(node)
        this.byCoordinateXMax.bind(node)
        this.byCoordinateXSorted.bind(node)
        return node.value.id!!
    }

    /**
     * Удаляет узел из коллекции
     * @param fromById нужно ли удалять вершину из [LabWorksCollection.byId]
     */
    private fun untie(node: Node, fromById: Boolean = true) {
        if (fromById)
            this.byId.untie(node)
        this.byMaximumPoint.untie(node)
        this.byCoordinateXMax.untie(node)
        this.byCoordinateXSorted.untie(node)
        node.value.id = null
    }

    /**
     * Добавляет лабораторную работу в коллекцию
     *
     * _Сложность: O(N)_ todo из-за очереди
     * @return [идентификатор][LabWork.id], присвоенный этой лабораторной работе
     */
    @Suppress("MemberVisibilityCanBePrivate", "KDocUnresolvedReference")
    fun add(work: LabWork) = this.bind(Node(work))

    /**
     * Обновляет лабораторную работу по заданному [идентификатору][LabWork.id]
     *
     * _Сложность: O(log N)_
     * @return возвращает лабораторную работу, которая была обновлена, иначе `null`
     */
    fun update(id: LabWorkId, work: LabWork): LabWork? {
        if (work.id != null)
            throw IllegalStateException("Лабораторная уже имеет владельца, добавление в коллекцию отменено")

        val node = this.byId[id] ?: return null//throw NoSuchElementException("Лабораторная работа с идентификатором $id не найдена")
        node.value.id = null
        work.id = id
        val old = node.value
        node.value = work
        return old
    }

    /**
     * Обновляет лабораторную работу по заданному [идентификатору][LabWork.id]
     *
     * _Сложность: O(log N)_
     * @return возвращает лабораторную работу, которая была обновлена, иначе `null`
     */
    @Suppress("NOTHING_TO_INLINE")
    inline operator fun set(id: LabWorkId, work: LabWork) = this.update(id, work)

    /**
     * Возвращает лабораторную работу с заданным [идентификатором][LabWork.id] или `null`, если такой не существует
     *
     * _Сложность: O(log N)_
     */
    operator fun get(id: LabWorkId): LabWork? = this.byId[id]?.value

    /**
     * Удаляет лабораторную работу с заданным [идентификатором][LabWork.id]
     *
     * _Сложность: O(log N)_
     * @return лабораторную работу которая была удалена, иначе `null`
     */
    @Suppress("unused")
    fun remove(id: LabWorkId): LabWork? = this.byId[id]?.also(this::untie)?.value

    /**
     * Удаляет все лабораторные работы из коллекции
     *
     * _Сложность: O(N), O(1) без высвобождения [идентификаторов][LabWork.id]_
     * @return `true` если лабораторная работа была найдена и удалена, иначе `false`
     */
    fun clear() {
        // this.byId.clear()
        // this.byMaximumPoint.clear()
        // this.byCoordinateXMax.clear()
        // this.byCoordinateXSorted.clear()

        while (!this.byId.isEmpty()) {
            this.untie(this.byId.top!!)
        }
    }

    /**
     * Итератор для получения всех лабораторных работ, удалённых функцией [LabWorksCollection.removeGreaterThanCoordinateX]
     */
    inner class RemoveGreaterThenCoordinateX(
        @Suppress("MemberVisibilityCanBePrivate")
        val key: Long
    ) : Iterator<LabWork> {
        override fun hasNext(): Boolean = this@LabWorksCollection.byCoordinateXMax.max?.let { node ->
            node.value.coordinates.x > this@RemoveGreaterThenCoordinateX.key
        } ?: false

        override fun next(): LabWork {
            val node = this@LabWorksCollection.byCoordinateXMax.max?.let { node ->
                return@let if (node.value.coordinates.x <= this@RemoveGreaterThenCoordinateX.key)
                    null
                else
                    node
            } ?: throw IllegalStateException("Все элементы со слишком большими координатами уже были удалены")
            this@LabWorksCollection.untie(node)
            return node.value
        }

        /**
         * Удаляет все оставшиеся элементы, удовлетворяющие условию
         * @return количество элементов, удалённых этой функцией
         */
        @Suppress("unused")
        fun processRemaining(): ULong {
            var count = 0uL
            for (work in this@RemoveGreaterThenCoordinateX) {
                count++
            }
            return count
        }
    }

    /**
     * Возвращает итератор, в ходе перебора которого лабораторные работы удаляются
     *
     * _Сложность: O(R)_
     */
    fun removeGreaterThanCoordinateX(key: Long) = this.RemoveGreaterThenCoordinateX(key)

    /**
     * Добавляет лабораторную работу в коллекцию, если её [координата X][Coordinates.x] больше чем максимальная из имеющихся в коллекции
     *
     * _Сложность: O(log N)_
     * @return `true` если элемент был добавлен, иначе `false`
     */
    @Suppress("MemberVisibilityCanBePrivate", "unused")
    fun addIfGreatestCoordinateX(work: LabWork): Boolean {
        this.byCoordinateXMax.max?.value?.coordinates?.x?.also { max ->
            if (work.coordinates.x <= max)
                return@addIfGreatestCoordinateX false
        }
        this.add(work)
        return true
    }

    /**
     * Возвращает итератор содержащий все лабораторные работы заданной [сложности][Difficulty]
     *
     * _Сложность: O(N)_
     */
    @Suppress("unused")
    fun getByDifficulty(difficulty: Difficulty): Iterator<LabWork> = this.byId.asSequence().mapNotNull { node -> if (node.value.difficulty == difficulty) node.value else null }.iterator()

    /**
     * Итератор, который "на лету" преобразовывает узлы в значения
     */
    @JvmInline
    private value class Node2ValueIterator(
        private val original: Iterator<Node>
    ) : Iterator<LabWork> {
        override fun hasNext(): Boolean = this.original.hasNext()

        override fun next(): LabWork = this.original.next().value

    }

    /**
     * Возвращает итератор содержащий лабораторные работы в отсортированном по убыванию [координаты X][Coordinates.x] порядке
     *
     * _Сложность: O(N)_
     */
    @Suppress("unused")
    fun descendingByCoordinateX(): Iterator<LabWork> = Node2ValueIterator(this.byCoordinateXSorted.sortedIterator())

    /**
     * Возвращает итератор содержащий лабораторные работы в отсортированном по убыванию [максимальной точкой][LabWork.maximumPoint] порядке
     *
     * _Сложность: O(N)_
     */
    @Suppress("unused")
    fun descendingByMaximumPoint(): Iterator<LabWork> = Node2ValueIterator(this.byMaximumPoint.sortedIterator())

    override operator fun iterator(): Iterator<LabWork> = Node2ValueIterator(this.byId.iterator())

    /**
     * Размер коллекции
     *
     * _Сложность: O(N)_
     */
    val size
        get() = this.byId.size

    /**
     * Проверяет что коллекция пуста
     */
    fun isEmpty() = this.byId.isEmpty()

    /**
     * Проверяет что коллекция не пуста
     */
    fun isNotEmpty() = this.byId.isNotEmpty()

    /**
     * Проверяет что идентификатор находится в коллекции
     */
    operator fun contains(id: LabWorkId) = id in this.byId
}