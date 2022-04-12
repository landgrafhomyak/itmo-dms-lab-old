package com.github.landgrafhomyak.itmo.dms_lab.io

/**
 * Предоставляет унифицированный доступ к потокам с запросами к базе данных
 */
interface ScriptScanner : Iterable<String>, Iterator<String> {
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
        while (this@ScriptScanner.hasNextRequest())
            yield(this@ScriptScanner.getNextRequest())
    }

    /**
     * @see ScriptScanner.hasNextRequest
     */
    override operator fun hasNext(): Boolean = this.hasNextRequest()

    /**
     * @see ScriptScanner.getNextRequest
     */
    override fun next(): String = this.getNextRequest()
}