package com.github.landgrafhomyak.itmo.dms_lab.collections

/**
 * Узел одного [бинарного (двоичного) дерева](https://ru.wikipedia.org/wiki/%D0%94%D0%B2%D0%BE%D0%B8%D1%87%D0%BD%D0%BE%D0%B5_%D0%B4%D0%B5%D1%80%D0%B5%D0%B2%D0%BE)
 * @param T тип узла
 * @param C тип (enum) цвета узла
 */
interface BinaryTreeLinksWithColor<T: Any, C : Any>: BinaryTreeLinks<T> {
    /**
     * Цвет узла
     */
    var color: C
}