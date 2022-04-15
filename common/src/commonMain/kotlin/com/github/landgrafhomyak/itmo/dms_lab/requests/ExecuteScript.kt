package com.github.landgrafhomyak.itmo.dms_lab.requests

import com.github.landgrafhomyak.itmo.dms_lab.io.ScriptSourceFactory

/**
 * Конечный объект запроса `execute_script`
 * @param factory конструктор потока скрипта
 * @sample ExecuteScript.help
 */
@Suppress("unused", "EqualsOrHashCode")
class ExecuteScript(
    @Suppress("MemberVisibilityCanBePrivate")
    val factory: ScriptSourceFactory
) : BoundRequest() {
    override val meta: RequestMeta
        get() = Meta

    companion object Meta : RequestMeta {
        override val id: String = "execute_script"
        override val help: String = "Выполняет скрипт из указанного источника"
    }



    @Suppress("CovariantEquals")
    override fun equals(other: BoundRequest): Boolean {
        if (other !is ExecuteScript) return false
        return this.factory == other.factory
    }

    override fun hashCode(): Int = this.factory.hashCode()
}