package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.ScriptSourceFactory

/**
 * Конечный объект команды `execute_script`
 * @param factory конструктор потока скрипта
 * @see Meta
 * @sample Meta.help
 */
@Suppress("unused")
class ExecuteScript(
    @Suppress("MemberVisibilityCanBePrivate")
    val factory: ScriptSourceFactory
) : BoundCommand(Meta) {
    object Meta : CommandMeta() {
        override val id: String = "execute_script"
        override val help: String = "Выполняет скрипт из указанного источника"
    }
}