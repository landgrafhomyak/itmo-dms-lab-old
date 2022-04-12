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
    private val collection: AbstractMutableLinkedCollection<N>,
    top: N?,
    private val linksGetter: N.() -> BinaryTreeLinks<N>
) : MutableIterator<N> {

    /**
     * Путь от вершины к узлу который будет возвращён следующим
     */
    private val stack = mutableListOf<N>()

    /**
     * Ссылка на последний возвращённый итератором элемент
     *
     * Равна `null` если дерево пустое или итерация не была начата
     */
    var last: N? = null

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
            @Suppress("SpellCheckingInspection")
            throw IllegalArgumentException("Итерирование бинарного дерева завершено")

        return this.stack.last().also { current ->
            this.stack.removeLast()
            this.linksGetter(current).right?.also(this::addNodeToStack)
            this.last = current
        }
    }

    override fun remove() {
        this.collection.untie(
            this.last ?: throw IllegalArgumentException("Состояние итератора не позволяет удалить элемент из дерева")
        )
    }
}