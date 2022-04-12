package com.github.landgrafhomyak.itmo.dms_lab.collections

@Suppress("unused")
internal class AbstractLinkedListPriorityQueueTest : PriorityQueueAbstractTest() {
    override fun <T : Comparable<T>> queue(block: PriorityQueue<T>.() -> Unit) = LinkedListPriorityQueue<T>().run(block)
}