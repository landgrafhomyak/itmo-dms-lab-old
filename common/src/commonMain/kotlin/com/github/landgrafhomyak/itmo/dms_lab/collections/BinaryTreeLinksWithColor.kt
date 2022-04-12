package com.github.landgrafhomyak.itmo.dms_lab.collections

/**
 * Ссылки на соседние узлы одного узла в [бинарном (двоичном) дереве](https://ru.wikipedia.org/wiki/%D0%94%D0%B2%D0%BE%D0%B8%D1%87%D0%BD%D0%BE%D0%B5_%D0%B4%D0%B5%D1%80%D0%B5%D0%B2%D0%BE)
 * @param N тип узла
 * @param C тип (enum) цвета узла
 * @see BinaryTreeLinks
 */
interface BinaryTreeLinksWithColor<N : Any, C> : BinaryTreeLinks<N> {
    /**
     * Цвет узла
     */
    var color: C
}