package com.github.landgrafhomyak.itmo.dms_lab

import kotlin.jvm.JvmStatic

/**
 * Перечисление всех команд содержащее метаинформацию
 */
enum class CommandTypes {
    EMPTY {
        override val id: String = ""
        override fun argParse(args: Array<String>): EmptyCommand = EmptyCommand
    },
    ;

    /**
     * Уникальный идентификатор команды
     */
    abstract val id: String

    /**
     * Конструктор объекта команды упакованной с аргументами
     */
    abstract fun argParse(args: Array<String>): BundledCommand?

    companion object {
        private val kw2obj = buildMap {
            @Suppress("RemoveRedundantQualifierName")
            for (obj in CommandTypes.values()) {
                this[obj.id] = obj
            }
        }

        /**
         * Возвращает член перечисления с заданным идентификатором или null, если такого не существует
         */
        @JvmStatic
        fun byKeyword(id: String): CommandTypes? = this.kw2obj[id]
    }
}