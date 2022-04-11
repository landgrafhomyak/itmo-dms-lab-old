package com.github.landgrafhomyak.itmo.dms_lab.collections

import kotlin.jvm.JvmName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue


/**
 * Тесты для [красно-чёрного дерева][AbstractRedBlackTree]
 */
internal class AbstractRedBlackTreeTest {
    /**
     * Узел дерева используемый в тестах
     */
    private class Node<T>(
        val key: T
    ) : BinaryTreeLinksWithColor<Node<T>, AbstractRedBlackTree.Color> {
        override var parent: Node<T>? = null
        override var left: Node<T>? = null
        override var right: Node<T>? = null
        override lateinit var color: AbstractRedBlackTree.Color
        override fun toString(): String = "<rb-tree node for tests key='${this.key}' children=(${if (this.left == null) ' ' else '*'})-(${if (this.right == null) ' ' else '*'})'>"
    }

    /**
     * Создаёт объект дерева использующего [нужные узлы][Node] и применяет к нему функцию (нужно исключительно для красоты)
     */
    private fun <T : Comparable<T>> tree(block: AbstractRedBlackTree<Node<T>, T>.() -> Unit) = AbstractRedBlackTree<Node<T>, T>(
        node@{ this@node },
        node@{ this@node.key }
    ).run(block)

    /**
     * Сокращение для более удобного добавления элементов в дерево
     */
    private fun <T : Comparable<T>> AbstractRedBlackTree<Node<T>, T>.add(element: T) = this@add.link(Node(element))

    /**
     * Сокращение для более удобного удаления элементов из дерева
     */
    private fun <T : Comparable<T>> AbstractRedBlackTree<Node<T>, T>.pop(element: T) = this@pop.exclude(this@pop[element]!!)

    /**
     * В дереве метод [AbstractRedBlackTree.contains] проверяет наличие ключа, а метод [AbstractRedBlackTree.iterator] возвращает узлы.
     * Поэтому [kotlin.test.assertContains] стандартной библиотеки не может правильно определить типы, а интерфейса [`Container`](https://docs.python.org/3/library/collections.abc.html#collections.abc.Container)
     * в Java (а следовательно и в Kotlin) не завезли
     */
    @Suppress("NothingToInline")
    private inline fun <T : Comparable<T>> assertContains(tree: AbstractRedBlackTree<Node<T>, T>, element: T, message: String? = null) {
        assertTrue(element in tree, message)
    }

    /**
     * Аналогично методу [AbstractRedBlackTreeTest.assertContains] проверяет что все элементы коллекции содержатся в дереве
     */
    @JvmName("assertContentEquals2")
    @Suppress("NothingToInline")
    private inline fun <T : Comparable<T>> assertContentEquals(tree: AbstractRedBlackTree<Node<T>, T>, elements: Iterable<T>) {
        @Suppress("NAME_SHADOWING")
        val elements = elements.toList()
        elements.forEachIndexed { index, elem ->
            assertContains(tree, elem, "Элемент №$index '$elem' отсутствует в дереве")
        }
        tree.assertSizeEquals(elements.size)
    }

    /**
     * Сокращение функции [AbstractRedBlackTreeTest.assertContentEquals] для использования внутри блока [AbstractRedBlackTreeTest.tree]
     */
    @JvmName("assertContentEquals1")
    @Suppress("NothingToInline")
    private inline fun <T : Comparable<T>> AbstractRedBlackTree<Node<T>, T>.assertContentEquals(elements: Iterable<T>) {
        this@AbstractRedBlackTreeTest.assertContentEquals(this@assertContentEquals, elements)
    }

    /**
     * Сокращение функции [kotlin.test.assertEquals] для проверки размера дерева внутри блока [AbstractRedBlackTreeTest.tree]
     */
    @Suppress("NothingToInline")
    private inline fun <T : Comparable<T>> AbstractRedBlackTree<Node<T>, T>.assertSizeEquals(expected: Int) {
        val actual = this@assertSizeEquals.size
        assertEquals(expected, this@assertSizeEquals.size, "Размер дерева ($actual) отличается от ожидаемого ($expected)")
    }

    /**
     * Проверяет что дерево хранит в себе добавленные в него элементы
     */
    @Test
    fun testHoldingsOne() = this.tree<Int> {
        add(0)
        assertContentEquals(listOf(0))
    }

    /**
     * Проверяет что дерево корректно хранит больше одного элемента
     */
    @Test
    fun testHoldingsFew() = this.tree<Int> {
        add(1)
        add(-1)
        add(0)
        assertContentEquals(-1..1)
    }

    /**
     * Проверяет что дерево корректно хранит много элементов
     */
    @Test
    fun testHoldingsMany() = this.tree<Int> {
        val elements = listOf(5, -2, 3, 6, 4, -10, -9, -11, 0, 55, -65, 54, -63, 22, -19)
        for (elem in elements)
            add(elem)
        assertContentEquals(elements)
    }

    /**
     * Проверяет что дерево корректно заменяет элементы с существующим ключём
     */
    @Test
    fun testDuplications() = this.tree<Int> {
        val node = Node(0)
        assertSame(null, link(node))
        assertSame(node, add(0))
        assertContentEquals(listOf(0))
    }

    /**
     * Проверяет что дерево корректно удаляет один элемент
     */
    @Test
    fun testRemoveOne() = this.tree<Int> {
        add(0)
        pop(0)
        assertContentEquals(listOf())
    }

    /**
     * Проверяет что дерево может удалить больше одного элемента
     */
    @Test
    fun testRemoveFew() = this.tree<Int> {
        add(1)
        add(-1)
        add(0)
        add(2)

        pop(0)
        pop(2)
        pop(-1)

        assertContentEquals(listOf(1))
    }

    /**
     * Проевряет что дерево может удалять произвольное количество произвольных элементов
     */
    @Test
    fun testRemoveMany() = this.tree<Int> {
        val elements = mutableSetOf(5, -2, 3, 6, 4, -10, -9, -11, 0, 55, -65, 54, -63, 22, -19)
        for (elem in elements)
            add(elem)

        pop(54); elements.remove(54)
        pop(6); elements.remove(6)
        pop(-10); elements.remove(-10)
        pop(22); elements.remove(22)
        pop(-65); elements.remove(-65)
        pop(0); elements.remove(0)
        assertContentEquals(elements)
    }

}