@file:Suppress("NothingToInline")

package com.github.landgrafhomyak.itmo.dms_lab.collections

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Тесты для [итератора бинарных деревьев][MutableBinaryTreeIterator]
 */
internal class BinaryTreeIteratorTest {
    /**
     * Узел дерева, используемого для тестов
     */
    @Suppress("EqualsOrHashCode")
    private class Node(
        override var left: Node?,
        override var right: Node?
    ) : BinaryTreeLinks<Node> {
        override var parent: Node? = null

        init {
            this.left?.parent = this
            this.right?.parent = this
        }

        override fun equals(other: Any?): Boolean = this === other
    }

    /**
     * Объект-пустышка для инициализации итератора
     */
    private object NoCollection : AbstractMutableLinkedCollection<Node> {
        override fun bind(node: Node): Node? = null

        override fun untie(node: Node) {}
        override fun iterator(): MutableIterator<Node> = object : MutableIterator<Node> {
            override fun hasNext(): Boolean = false
            override fun next(): Node = throw  IllegalStateException()
            override fun remove() {}

        }
    }

    /**
     * Создаёт итератор для переданного дерева (вершины дерева) и делает над ним необходимые преобразования для использования в тестировании
     */
    private fun wrapIterator(top: Node?) = MutableBinaryTreeIterator(NoCollection, top) node@{ this@node }.asSequence()

    /**
     * Проверка на то что итератор пустого дерева (отсутствует вершина) ничего не возвращает
     */
    @Test
    fun testEmptyIterator() {
        assertContentEquals(
            this.wrapIterator(null),
            sequenceOf(),
            "Пустое дерево"
        )
    }

    @Suppress("SpellCheckingInspection")
    /**
     * Проверка на то что итератор не даёт прочитать лишние узлы (после того как итерирование завершено)
     */
    @Test
    fun testEmptyFails() {
        assertFailsWith(IllegalArgumentException::class) {
            MutableBinaryTreeIterator(NoCollection, null) node@{ this@node }.next()
        }
    }


    private fun iterateNodeRecursive(node: Node): Sequence<Node> = sequence {
        node.left?.apply child@{ yieldAll(iterateNodeRecursive(this@child)) }
        yield(node)
        node.right?.apply child@{ yieldAll(iterateNodeRecursive(this@child)) }
    }

    /**
     * Проверяет корректность работы итератора на сбалансированном дереве (в виде треугольника)
     */
    @Test
    fun testBalancedTree() {
        val treeHeight = 4
        assertTrue(treeHeight > 1, "Высота дерева для теста должен быть 2 или больше")
        val treeSize = (1 shl treeHeight) + 1

        val nodes = MutableList<Node?>(treeSize) { null }
        for (i in (treeSize / 2) until treeSize)
            nodes[i] = Node(null, null)
        for (i in (treeSize / 2 - 1) downTo 0) {
            nodes[i] = Node(nodes[i * 2 + 1], nodes[i * 2 + 2])
        }

        assertContentEquals(
            this.wrapIterator(nodes[0]!!),
            iterateNodeRecursive(nodes[0]!!),
            "Сбалансированное дерево высоты $treeHeight ($treeSize узлов)"
        )

    }

    /**
     * Проверяет корректность работы итератора на произвольном дереве
     */
    @Test
    fun testRandomTree() {
        val top: Node = run treeBuilder@{
            val childLLLLLL = Node(null, null)
            val childLLLLL = Node(childLLLLLL, null)
            val childLLLL = Node(childLLLLL, null)
            val childLLL = Node(childLLLL, null)
            val childLL = Node(childLLL, null)


            val childLRLR = Node(null, null)
            val childLRL = Node(null, childLRLR)

            val childLRRL = Node(null, null)
            val childLRR = Node(childLRRL, null)

            val childLR = Node(childLRL, childLRR)


            val childL = Node(childLL, childLR)


            val childRL = Node(null, null)
            val childRRR = Node(null, null)
            val childRR = Node(null, childRRR)

            val childR = Node(childRL, childRR)

            return@treeBuilder Node(childL, childR)
        }

        assertContentEquals(
            this.wrapIterator(top),
            iterateNodeRecursive(top),
            "Произвольное дерево из (${iterateNodeRecursive(top).count()} узлов)"
        )
    }
}