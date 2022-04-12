package com.github.landgrafhomyak.itmo.dms_lab.collections

import kotlin.jvm.JvmName
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Тесты для очереди с приоритетом
 */
abstract class PriorityQueueAbstractTest {
    /**
     * Создаёт объект дерева использующего нужные узлы и применяет к нему функцию (нужно исключительно для красоты)
     */
    protected abstract fun <T : Comparable<T>> queue(
        @Suppress("UNUSED_PARAMETER")
        block: PriorityQueue<T>.() -> Unit
    )

    /**
     * Проверяет что все элементы коллекции содержатся в дереве
     */
    @JvmName("assertContentEquals2")
    @Suppress("NothingToInline")
    protected inline fun <T : Comparable<T>> assertContentEquals(tree: PriorityQueue<T>, elements: Iterable<T>) {
        @Suppress("NAME_SHADOWING")
        val elements = elements.toSet()
        assertEquals(elements, tree.toSet())
        tree.assertSizeEquals(elements.size)
    }

    /**
     * Сокращение функции [PriorityQueueAbstractTest.assertContentEquals] для использования внутри блока [PriorityQueueAbstractTest.queue]
     */
    @JvmName("assertContentEquals1")
    @Suppress("NothingToInline")
    protected inline fun <T : Comparable<T>> PriorityQueue<T>.assertContentEquals(elements: Iterable<T>) {
        this@PriorityQueueAbstractTest.assertContentEquals(this@assertContentEquals, elements)
    }

    /**
     * Сокращение функции [kotlin.test.assertEquals] для проверки размера дерева внутри блока [PriorityQueueAbstractTest.queue]
     */
    @Suppress("NothingToInline")
    protected inline fun <T : Comparable<T>> PriorityQueue<T>.assertSizeEquals(expected: Int) {
        @Suppress("RedundantAsSequence")
        val actual = this@assertSizeEquals.asSequence().count()
        assertEquals(expected, actual, "Размер дерева ($actual) отличается от ожидаемого ($expected)")
    }

    @Test
    fun addingElements() = this.queue<Int> {
        val elements = listOf(5, -2, 3, 6, 4, -10, -9, -11, 0, 55, -65, 54, -63, 22, -19)
        for (elem in elements) {
            push(elem)
        }
        assertContentEquals(elements)
    }

    @Test
    fun extractingElements() = this.queue<Int> {
        val elements = listOf(5, -2, 3, 6, 4, -10, -9, -11, 0, 55, -65, 54, -63, 22, -19)
        for (elem in elements) {
            push(elem)
        }

        kotlin.test.assertContentEquals(
            elements.sorted().asReversed(),
            buildList list@{
                while (!this@queue.isEmpty()) {
                    this@list.add(this@queue.pop())
                }
            }
        )
    }

}