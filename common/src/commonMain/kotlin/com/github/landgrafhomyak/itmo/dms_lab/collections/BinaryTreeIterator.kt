package com.github.landgrafhomyak.itmo.dms_lab.collections


/**
 * Итератор для бинарных (двоичных) деревьев
 *
 * Перебирает узлы в порядке **(левое поддерево)-(узел)-(правое поддерево)**
 *
 * @param top вершина (корень) дерева
 * @param linksGetter геттер ссылок на связанные узлы
 * @see BinaryTreeIterator.addNodeToStack
 */
class BinaryTreeIterator<N : Any>(
    top: N?,
    private val linksGetter: N.() -> BinaryTreeLinks<N>
) : Iterator<N> {

    /**
     * Путь от вершины к узлу который будет возвращён следующим
     */
    private val stack = mutableListOf<N>()

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
        }
    }
}