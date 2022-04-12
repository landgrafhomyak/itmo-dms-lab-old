package com.github.landgrafhomyak.itmo.dms_lab.collections

/**
 * Тесты для [очереди с приоритетом на двусвязном списке][LinkedListPriorityQueue]
 */
@Suppress("unused", "SpellCheckingInspection")
internal class AbstractLinkedListPriorityQueueTest : PriorityQueueAbstractTest() {
    override fun <T : Comparable<T>> queue(block: PriorityQueue<T>.() -> Unit) = LinkedListPriorityQueue<T>().run(block)
}