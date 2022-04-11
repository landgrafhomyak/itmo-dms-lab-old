package com.github.landgrafhomyak.itmo.dms_lab.collections

/**
 * Интерфейс для коллекций использующих связывание своих элементов по ссылкам
 *
 * @param N тип узла
 */
interface MutableLinkedCollection<N : Any> {
    /**
     * Аналог функций `add`, `put`, `push` и т.д.
     *
     * Связывает данный **узел** с коллекцией. Так как целевая коллекция является объединением нескольких более простых,
     * добавление непосредственно элемента (и создание для него нового узла-обёртки внутри этого класса) некорректно
     *
     * @return старый узел с таким же ключом (все ссылки будут обнулены), если существовал
     */
    fun link(node: N): N?

    /**
     * Аналог функций `pop`, `remove`, `delete` и т.д.
     *
     * Отвязывает данный **узел** от коллекции **без проверки на принадлежность**
     * (проверка должна происходить на этапе получения узла).
     * Так как поддержка произвольного доступа к элементам коллекции (например, по
     * ключу) не является обязательным методом для связных структур, то удаление возможно только по узлу
     *
     */
    fun exclude(node: N)
}