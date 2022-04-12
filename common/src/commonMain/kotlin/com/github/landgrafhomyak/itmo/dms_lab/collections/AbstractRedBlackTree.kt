package com.github.landgrafhomyak.itmo.dms_lab.collections

import kotlin.jvm.JvmName

/**
 * Абстрактная реализация [красно-чёрного дерева](https://ru.wikipedia.org/wiki/%D0%9A%D1%80%D0%B0%D1%81%D0%BD%D0%BE-%D1%87%D1%91%D1%80%D0%BD%D0%BE%D0%B5_%D0%B4%D0%B5%D1%80%D0%B5%D0%B2%D0%BE)
 * (словаря) без привязки к классу узла
 * @param N класс узла, содержащий [ссылки на связанные узлы][BinaryTreeLinksWithColor] и ключ (или объект с содержащий ключ)
 * @param K элемент коллекции, должен быть наследником интерфейса [Comparable]
 *
 * ```
 * when (ключ.compareTo(узел.ключ)) {
 *     < 0  -> левое поддерево
 *     > 0  -> правое поддерево
 *     == 0 -> узел найден
 * }
 * ```
 * @param linksGetter гетер извлекающий [ссылки на связанные узлы и цвет][BinaryTreeLinksWithColor] из данного узла
 * @param keyGetter гетер извлекающий ключ из данного узла
 * @see AbstractRedBlackTree.bind
 * @see RedBlackTreeMap
 * @see RedBlackTreeSet
 * @see RedBlackTreeSetWithKeyAccess
 */
class AbstractRedBlackTree<N : Any, K : Comparable<K>>(
    private val linksGetter: N.() -> BinaryTreeLinksWithColor<N, Color>, private val keyGetter: N.() -> K
) : AbstractMutableLinkedCollection<N>, MutableIterable<N> {
    /**
     * Цвета узлов
     */
    enum class Color {
        /**
         * Красный
         */
        RED,

        /**
         * Чёрный
         */
        BLACK
    }

    /**
     * Вершина дерева
     */
    var top: N? = null

    /**
     * Сокращение для поля [BinaryTreeLinksWithColor.parent]
     */
    private inline var N.parent: N?
        get() = this@AbstractRedBlackTree.linksGetter(this).parent
        set(value) {
            this@AbstractRedBlackTree.linksGetter(this).parent = value
        }

    /**
     * Сокращение для поля [BinaryTreeLinksWithColor.left]
     */
    private inline var N.left: N?
        get() = this@AbstractRedBlackTree.linksGetter(this).left
        set(value) {
            this@AbstractRedBlackTree.linksGetter(this).left = value
        }

    /**
     * Сокращение для поля [BinaryTreeLinksWithColor.right]
     */
    private inline var N.right: N?
        get() = this@AbstractRedBlackTree.linksGetter(this).right
        set(value) {
            this@AbstractRedBlackTree.linksGetter(this).right = value
        }

    /**
     * Возвращает прародителя (дедушку) узла, если такой существует
     */
    private inline val N.grandparent: N?
        get() = this.parent?.parent

    /**
     * Проверка на то что узел не равен `null` и является левым ребёнком
     */
    @get:JvmName("isLeftNullable")
    private inline val N?.isLeft: Boolean
        get() = this != null && this.isLeft

    /**
     * Проверка на то что узел не равен `null` и является правым ребёнком
     */
    @get:JvmName("isRightNullable")
    private inline val N?.isRight: Boolean
        get() = this != null && this.isRight

    /**
     * Проверка на то что узел является левым ребёнком
     */
    @get:JvmName("isLeft")
    private inline val N.isLeft: Boolean
        get() = this === this.parent?.left

    /**
     * Проверка на то что узел является правым ребёнком
     */
    @get:JvmName("isRight")
    private inline val N.isRight: Boolean
        get() = this === this.parent?.right

    /**
     * Возвращает брата узла (другого ребёнка [родителя][parent]), если такой существует
     */
    private inline val N.sibling: N?
        get() = when {
            this.isLeft  -> this.parent?.right
            this.isRight -> this.parent?.left
            else         -> null
        }

    /**
     * Возвращает дядю узла (брата родителя), если такой существует
     */
    private inline val N.uncle: N?
        get() = this.parent?.sibling


    /**
     * Сокращение для поля [BinaryTreeLinksWithColor.color]
     */
    private inline var N.color: Color
        get() = this@AbstractRedBlackTree.linksGetter(this).color
        set(value) {
            this@AbstractRedBlackTree.linksGetter(this).color = value
        }

    /**
     * Сокращение [гетера для извлечения ключа][keyGetter] из узла
     */
    private inline val N.key: K
        get() = this@AbstractRedBlackTree.keyGetter(this)

    /**
     * Левый поворот вокруг вершины
     * @see AbstractRedBlackTree.rotateRight
     */
    @Suppress("DuplicatedCode")
    private fun N.rotateLeft() {
        val pivot = this@rotateLeft.right!!

        pivot.parent = this@rotateLeft.parent
        if (pivot.parent == null) this@AbstractRedBlackTree.top = pivot

        when {
            this@rotateLeft.isLeft  -> this@rotateLeft.parent?.left = pivot
            this@rotateLeft.isRight -> this@rotateLeft.parent?.right = pivot
        }

        this@rotateLeft.right = pivot.left
        pivot.left?.parent = this@rotateLeft

        this@rotateLeft.parent = pivot
        pivot.left = this@rotateLeft
    }

    /**
     * Правый поворот вокруг вершины
     * @see AbstractRedBlackTree.rotateLeft
     */
    @Suppress("DuplicatedCode")
    private fun N.rotateRight() {
        val pivot = this@rotateRight.left!!

        pivot.parent = this@rotateRight.parent
        if (pivot.parent == null) this@AbstractRedBlackTree.top = pivot

        when {
            this@rotateRight.isLeft  -> this@rotateRight.parent?.left = pivot
            this@rotateRight.isRight -> this@rotateRight.parent?.right = pivot
        }

        this@rotateRight.left = pivot.right
        pivot.right?.parent = this@rotateRight

        this@rotateRight.parent = pivot
        pivot.right = this@rotateRight
    }

    /**
     * Встраивает вершину на место переданной. Ссылки старой вершины обнуляются, если это не одна и та же вершина
     */
    private fun N.putInsteadOf(other: N) {
        if (this@AbstractRedBlackTree === other) {
            return
        }

        this@putInsteadOf.parent = other.parent?.also { parent ->
            when {
                other.isLeft  -> parent.left = this@putInsteadOf
                other.isRight -> parent.right = this@putInsteadOf
            }
        }

        this@putInsteadOf.left = other.left
        other.left?.parent = this@putInsteadOf

        this@putInsteadOf.right = other.right
        other.right?.parent = this@putInsteadOf

        this@putInsteadOf.color = other.color

        other.parent = null
        other.left = null
        other.right = null
    }

    /**
     * Возвращает узел, который содержит данный ключ
     */
    operator fun get(key: K): N? {
        var pointer = this.top
        while (pointer != null) {
            when {
                key < pointer.key  -> pointer = pointer.left
                key > pointer.key  -> pointer = pointer.right
                key == pointer.key -> return pointer
            }
        }
        return null
    }

    override fun bind(node: N): N? {
        node.parent = null
        node.left = null
        node.right = null
        node.color = Color.RED

        var pointer: N? = this.top ?: run {
            this.top = node
            node.color = Color.BLACK
            return@bind null
        }

        lateinit var prevPointer: N
        while (pointer != null) {
            prevPointer = pointer
            when {
                node.key < pointer.key  -> pointer = pointer.left
                node.key > pointer.key  -> pointer = pointer.right
                node.key == pointer.key -> {
                    node.putInsteadOf(pointer)
                    return pointer
                }
            }
        }

        when {
            node.key < prevPointer.key -> prevPointer.left = node
            node.key > prevPointer.key -> prevPointer.right = node
        }
        node.parent = prevPointer

        @Suppress("NAME_SHADOWING")
        var node: N = node
        do {
            if (node.parent?.color == Color.BLACK) {
                return null
            }

            if (node.uncle?.color == Color.RED) {
                node.parent!!.color = Color.BLACK
                node.uncle!!.color = Color.BLACK
                node.grandparent!!.color = Color.RED

                node = node.grandparent!!

                if (node.parent == null) {
                    node.color = Color.BLACK
                    return null
                }

                continue
            }
            break

        } while (true)

        when {
            node.isRight && node.parent.isLeft -> {
                node.parent!!.rotateLeft()
                node = node.left!!
            }
            node.isLeft && node.parent.isRight -> {
                node.parent!!.rotateRight()
                node = node.right!!
            }
        }

        node.parent!!.color = Color.BLACK
        node.grandparent!!.color = Color.RED


        when {
            node.isLeft && node.parent.isLeft -> node.grandparent!!.rotateRight()
            else                              -> node.grandparent!!.rotateLeft()
        }

        return null
    }

    override fun untie(node: N) {
        if (node.left == null && node.right == null) {
            if (node.parent == null) {
                this.top = null
            } else {
                when {
                    node.isLeft  -> node.parent!!.left = null
                    node.isRight -> node.parent!!.right = null
                }
                node.parent = null
            }
            return
        }

        @Suppress("NAME_SHADOWING", "DuplicatedCode")
        var node: N = run {
            var pointer: N
            val child: N? = when {
                node.right != null -> {
                    pointer = node.right!!
                    while (true) {
                        pointer.left?.apply child@{ pointer = this@child } ?: break
                    }
                    when {
                        pointer.isLeft  -> pointer.parent!!.left = pointer.right
                        pointer.isRight -> pointer.parent!!.right = pointer.right
                    }
                    pointer.right?.parent = pointer.parent
                    pointer.right
                }
                else               -> {
                    pointer = node.left!!
                    while (true) {
                        pointer.right?.apply child@{ pointer = this@child } ?: break
                    }

                    when {
                        pointer.isLeft  -> pointer.parent!!.left = pointer.left
                        pointer.isRight -> pointer.parent!!.right = pointer.left
                    }
                    pointer.left?.parent = pointer.parent
                    pointer.left
                }
            }
            pointer.putInsteadOf(node)
            if (this.top === node) {
                this.top = pointer
            }
            child ?: return@untie

            if (pointer.color == Color.BLACK && child.color == Color.RED) {
                child.color = Color.BLACK
                return@untie
            }
            return@run child
        }

        while (true) {
            if (node.parent == null) {
                return
            }
            if (node.sibling?.color == Color.RED) {
                node.parent?.color = Color.BLACK
                node.sibling?.color = Color.BLACK
                when {
                    node.isLeft  -> node.parent!!.rotateLeft()
                    node.isRight -> node.parent!!.rotateRight()
                }
            }

            if (node.parent?.color == Color.BLACK && node.sibling?.color == Color.BLACK && node.sibling?.left?.color == Color.BLACK && node.sibling?.right?.color == Color.BLACK) {
                node.sibling!!.color = Color.RED
                node = node.parent!!
                continue
            }
            break
        }

        if (node.parent?.color == Color.RED && node.sibling?.color == Color.BLACK && node.sibling?.left?.color == Color.BLACK && node.sibling?.right?.color == Color.BLACK) {
            node.sibling!!.color = Color.RED
            node.parent!!.color = Color.BLACK
            return
        }


        if (node.sibling?.color == Color.RED) {
            if (node.isLeft && node.sibling?.right?.color == Color.BLACK && node.sibling?.left?.color == Color.RED) {
                node.sibling!!.color = Color.RED
                node.sibling!!.left!!.color = Color.BLACK
                node.rotateRight()
            } else if (node.isRight && node.sibling?.left?.color == Color.BLACK && node.sibling?.right?.color == Color.RED) {
                node.sibling!!.color = Color.RED
                node.sibling!!.right!!.color = Color.BLACK
                node.rotateLeft()
            }
        }

        node.sibling!!.color = node.parent!!.color
        node.parent!!.color = Color.BLACK

        when {
            node.isLeft  -> {
                node.sibling!!.right?.color = Color.BLACK
                node.parent!!.rotateLeft()
            }
            node.isRight -> {
                node.sibling!!.left?.color = Color.BLACK
                node.parent!!.rotateRight()
            }
        }
    }

    operator fun contains(key: K): Boolean = this[key] != null

    @Suppress("unused")
    fun isEmpty(): Boolean = this.top == null

    @Suppress("DuplicatedCode", "NothingToInline")
    private inline fun mutableSortedIterator(): MutableIterator<N> = MutableBinaryTreeIterator(this, this.top, this.linksGetter)

    @Suppress("MemberVisibilityCanBePrivate")
    fun sortedIterator(): Iterator<N> = this.mutableSortedIterator()

    override operator fun iterator(): MutableIterator<N> = this.mutableSortedIterator()

    fun sorted() = this.sortedIterator().asSequence().toList()

    fun clear() {
        this.top = null
    }

    val size: Int
        get() = this.asIterable().count()
}