package com.github.landgrafhomyak.itmo.dms_lab.collections

/**
 * Интерфейс [дека (двусторонней очереди)](https://ru.wikipedia.org/wiki/%D0%94%D0%B2%D1%83%D1%85%D1%81%D1%82%D0%BE%D1%80%D0%BE%D0%BD%D0%BD%D1%8F%D1%8F_%D0%BE%D1%87%D0%B5%D1%80%D0%B5%D0%B4%D1%8C)
 */
interface Deque<E> : Queue<E>, MutableIterable<E> {
    /**
     * Добавляет элемент в конец дека
     *
     * Обратная совместимость с [обычной очередью][Queue]
     */
    @Deprecated("Используйте функцию с явным указанием конца дека", ReplaceWith("pushBack"))
    override fun push(element: E) = this.pushBack(element)


    /**
     * Добавляет элемент в конец дека
     */
    fun pushBack(element: E)

    /**
     * Добавляет элемент в начало дека
     */
    fun pushFront(element: E)

    /**
     * Удаляет элемент из начала дека
     *
     * Обратная совместимость с [обычной очередью][Queue]
     * @return удалённый элемент или `null`, если очередь пуста
     */
    @Deprecated("Используйте функцию с явным указанием конца дека", ReplaceWith("popFrontOrNull"))
    override fun popOrNull(): E? = this.popFrontOrNull()

    /**
     * Удаляет элемент из конца дека
     * @return удалённый элемент или `null`, если дек пустой
     */
    fun popBackOrNull(): E?

    /**
     * Удаляет элемент из начала дека
     * @return удалённый элемент или `null`, если дек пустой
     */
    fun popFrontOrNull(): E?

    /**
     * Удаляет элемент из начала дека или кидает ошибку, если дек пустой
     *
     * Обратная совместимость с [обычной очередью][Queue]
     * @return удалённый элемент
     */
    @Suppress("DEPRECATION")
    @Deprecated("Используйте функцию с явным указанием конца дека", ReplaceWith("popFront"))
    override fun pop(): E = this.popOrNull() ?: throw NoSuchElementException("Нельзя удалить элемент из пустого дека")

    /**
     * Удаляет элемент из конца дека или кидает ошибку, если дек пустой
     * @return удалённый элемент
     */
    fun popBack(): E = this.popBackOrNull() ?: throw NoSuchElementException("Нельзя удалить элемент из пустого дека")

    /**
     * Удаляет элемент из начала дека или кидает ошибку, если дек пустой
     * @return удалённый элемент
     */
    fun popFront(): E = this.popFrontOrNull() ?: throw NoSuchElementException("Нельзя удалить элемент из пустого дека")

    /**
     * Первый элемент в деке или `null`, если дек пустой
     *
     * Обратная совместимость с [обычной очередью][Queue]
     */
    @Deprecated("Используйте поле с явным указанием конца дека", ReplaceWith("frontOrNull"))
    override val firstOrNull: E?
        get() = this.frontOrNull

    /**
     * Первый элемент в деке или кидается ошибка, если дек пустой
     *
     * Обратная совместимость с [обычной очередью][Queue]
     */
    @Suppress("DEPRECATION")
    @Deprecated("Используйте поле с явным указанием конца дека", ReplaceWith("front"))
    override val first: E
        get() = this.firstOrNull ?: throw NoSuchElementException("Нельзя получить элемент из пустого дека")


    /**
     * Первый элемент в деке или `null`, если дек пустой
     */
    val frontOrNull: E?


    /**
     * Первый элемент в деке или кидается ошибка, если дек пустой
     */
    val front: E
        get() = this.frontOrNull ?: throw NoSuchElementException("Нельзя получить элемент из пустого дека")

    /**
     * Последний элемент в деке или `null`, если дек пустой
     */
    val backOrNull: E?

    /**
     * Последний элемент в деке или кидается ошибка, если дек пустой
     */
    val back: E
        get() = this.backOrNull ?: throw NoSuchElementException("Нельзя получить элемент из пустого дека")


    /**
     * Проверка, что дек пустой
     */
    @Suppress("RedundantOverride")
    override fun isEmpty() = super.isEmpty()

    /**
     * Проверка, что дек не пустой
     */
    @Suppress("RedundantOverride")
    override fun isNotEmpty() = super.isNotEmpty()

    /**
     * Удаляет все элементы из дека
     */
    override fun clear()
}