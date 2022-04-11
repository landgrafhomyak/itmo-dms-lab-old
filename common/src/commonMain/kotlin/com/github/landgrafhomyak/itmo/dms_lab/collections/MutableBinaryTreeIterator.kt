package com.github.landgrafhomyak.itmo.dms_lab.collections


/**
 * Итератор для бинарных (двоичных) деревьев
 *
 * Перебирает узлы в порядке _**(левое поддерево)-(узел)-(правое поддерево)**_
 *
 * @param top вершина (корень) дерева
 * @param linksGetter геттер извлекающий [ссылки на связанные узлы][BinaryTreeLinksWithColor] из данного узла
 * @see MutableBinaryTreeIterator.addNodeToStack
 */
class MutableBinaryTreeIterator<N : Any>(
    private val collection: MutableLinkedCollection<N>,
    top: N?,
    private val linksGetter: N.() -> BinaryTreeLinks<N>
) : MutableIterator<N> {

    /**
     * Путь от вершины к узлу который будет возвращён следующим
     */
    private val stack = mutableListOf<N>()
    lateinit var last: N

    init {
        if (top != null) {
            this.addNodeToStack(top)
        }
    }

    /**
     * Добавляет узел в [стек][stack] и его левого ребёнка до тех пор, пока он существует. Таким образом на вершине [стека][stack] всегда находится
     * элемент который, будет возвращён следующим. После его удаления из [стека][stack] должна быть вызвана эта функция для его правого ребёнка
     */
    private fun addNodeToStack(node: N) {
        this.stack.add(node)
        while (true) {
            this.stack.add(this.linksGetter(this.stack.last()).left ?: break)
        }
    }

    override fun hasNext(): Boolean = this.stack.isNotEmpty()

    override fun next(): N {
        if (this.stack.isEmpty())
            throw IllegalArgumentException("Все узлы дерева были уже перебраны")

        return this.stack.last().also { current ->
            this.stack.removeLast()
            this.linksGetter(current).right?.also(this::addNodeToStack)
            this.last = current
        }
    }

    override fun remove() {
        this.collection.exclude(this.last)
    }
}