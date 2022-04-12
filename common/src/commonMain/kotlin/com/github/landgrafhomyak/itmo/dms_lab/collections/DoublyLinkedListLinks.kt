package com.github.landgrafhomyak.itmo.dms_lab.collections

@Suppress("SpellCheckingInspection")
/**
 * Ссылки на соседние узлы одного узла в [двусвязном списке](https://ru.wikipedia.org/wiki/%D0%A1%D0%B2%D1%8F%D0%B7%D0%BD%D1%8B%D0%B9_%D1%81%D0%BF%D0%B8%D1%81%D0%BE%D0%BA)
 * @param N тип узла
 * @see SinglyLinkedListLinks
 */
interface DoublyLinkedListLinks<N : Any> : SinglyLinkedListLinks<N> {
    /**
     * Предыдущий элемент в списке
     */
    var prev: N?
}