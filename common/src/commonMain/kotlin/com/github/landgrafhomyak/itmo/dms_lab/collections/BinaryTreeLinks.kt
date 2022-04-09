package com.github.landgrafhomyak.itmo.dms_lab.collections

/**
 * Узел одного [бинарного (двоичного) дерева](https://ru.wikipedia.org/wiki/%D0%94%D0%B2%D0%BE%D0%B8%D1%87%D0%BD%D0%BE%D0%B5_%D0%B4%D0%B5%D1%80%D0%B5%D0%B2%D0%BE)
 *
 * @param T тип узла
 */
interface BinaryTreeLinks<T : Any> {
    /**
     * Родительский узел
     */
    var parent: T?

    /**
     * Вершина левого поддерева
     */
    var left: T?

    /**
     * Вершина правого поддерева
     */
    var right: T?
}