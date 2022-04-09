package com.github.landgrafhomyak.itmo.dms_lab.io


/**
 * Печать сообщений в стандартный поток вывода без какого либо форматирования
 * @see RawConsoleScriptInput
 */
object RawConsoleScriptOutput : ScriptOutput {
    override fun info(message: String) {
        println(message)
    }

    override fun warning(message: String) {
        println(message)
    }

    override fun error(message: String) {
        println(message)
    }

    override fun request(level: UInt, request: String) {
        println(request)
    }
}
