package com.github.landgrafhomyak.itmo.dms_lab.io

/**
 * Чтение запросов из стандартного потока ввода без какого либо форматирования
 * @see RawConsoleScriptOutput
 */
object RawConsoleScriptInput : BufferedScriptInput() {
    override fun getNextRequestRaw(): String? = readlnOrNull()

    override fun onClosed(): Nothing = throw IllegalStateException("Стандартный поток вывода закрыт, чтение команд невозможно")
}
