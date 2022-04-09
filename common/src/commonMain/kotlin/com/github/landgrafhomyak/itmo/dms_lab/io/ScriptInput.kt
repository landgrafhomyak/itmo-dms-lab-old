package com.github.landgrafhomyak.itmo.dms_lab.io

/**
 * Предоставляет унифицированный доступ к потокам с запросами к базе данных
 */
interface ScriptInput : Iterable<String>, Iterator<String> {
    /**
     * Проверяет на наличие необработанных запросов в потоке
     */
    fun hasNextRequest(): Boolean

    /**
     * Возвращает строку со следующим запросом (после возврата функции считается обработанным)
     */
    fun getNextRequest(): String

    /**
     * Итерирует строки во входном потоке
     */
    override operator fun iterator(): Iterator<String> = iterator {
        while (this@ScriptInput.hasNextRequest())
            yield(this@ScriptInput.getNextRequest())
    }

    /**
     * @see ScriptInput.hasNextRequest
     */
    override operator fun hasNext(): Boolean = this.hasNextRequest()

    /**
     * @see ScriptInput.getNextRequest
     */
    override fun next(): String = this.getNextRequest()
}