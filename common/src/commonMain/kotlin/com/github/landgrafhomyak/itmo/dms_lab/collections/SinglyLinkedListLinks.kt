package com.github.landgrafhomyak.itmo.dms_lab.collections

/**
 * Ссылки на соседние узлы одного узла в [односвязном списке](https://ru.wikipedia.org/wiki/%D0%9E%D0%B4%D0%BD%D0%BE%D1%81%D0%B2%D1%8F%D0%B7%D0%BD%D1%8B%D0%B9_%D1%81%D0%BF%D0%B8%D1%81%D0%BE%D0%BA)
 * @param N тип узла
 */
interface SinglyLinkedListLinks<N : Any> {
    /**
     * Следующий элемент в списке
     */
    var next: N?
}