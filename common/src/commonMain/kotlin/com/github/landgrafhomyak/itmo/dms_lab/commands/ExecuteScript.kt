package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.ScriptSourceFactory

/**
 * Конечный объект запроса `execute_script`
 * @param factory конструктор потока скрипта
 * @sample ExecuteScript.help
 */
@Suppress("unused")
class ExecuteScript(
    @Suppress("MemberVisibilityCanBePrivate")
    val factory: ScriptSourceFactory
) : BoundRequest(Meta) {
    companion object Meta : RequestMeta {
        override val id: String = "execute_script"
        override val help: String = "Выполняет скрипт из указанного источника"
    }
}