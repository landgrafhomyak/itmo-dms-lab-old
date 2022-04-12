package com.github.landgrafhomyak.itmo.dms_lab.collections


/**
 * Абстрактная реализация [очереди с приоритетом](https://ru.wikipedia.org/wiki/%D0%9E%D1%87%D0%B5%D1%80%D0%B5%D0%B4%D1%8C_%D1%81_%D0%BF%D1%80%D0%B8%D0%BE%D1%80%D0%B8%D1%82%D0%B5%D1%82%D0%BE%D0%BC_(%D0%BF%D1%80%D0%BE%D0%B3%D1%80%D0%B0%D0%BC%D0%BC%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5))
 * на основе [кучи](https://ru.wikipedia.org/wiki/%D0%9A%D1%83%D1%87%D0%B0_(%D1%81%D1%82%D1%80%D1%83%D0%BA%D1%82%D1%83%D1%80%D0%B0_%D0%B4%D0%B0%D0%BD%D0%BD%D1%8B%D1%85))
 * (классическая куча пишется на массиве/списке, на связном дереве алгоритма пока что ещё не существует)
 * @see AbstractLinkedListPriorityQueue
 */
@Suppress("unused")
private class AbstractLinkedHeapPriorityQueue<N : Any, K : Comparable<K>> private constructor(
    private val linksGetter: N.() -> BinaryTreeLinks<N>,
    private val keyGetter: N.() -> K
) : AbstractMutableLinkedCollection<N> {
    override fun iterator(): MutableIterator<N> {
        TODO("Not yet implemented")
    }

    override fun bind(node: N): N? {
        TODO("Not yet implemented")
    }

    override fun untie(node: N) {
        TODO("Not yet implemented")
    }

}