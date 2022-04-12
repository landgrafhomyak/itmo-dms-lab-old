package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.collections.RedBlackTreeSetWithKeyAccess
import com.github.landgrafhomyak.itmo.dms_lab.io.Logger
import kotlin.jvm.JvmStatic

/**
 * Перечисление всех команд содержащее метаинформацию
 */
enum class CommandMeta {

    HELP {
        override val id: String = "help"
        override val help: String = "Выводит справку по доступным командам"
        override fun argParse(logger: Logger, args: Array<String>): Help = Help
    },
    INFO {
        override val id: String = "info"
        override val help: String = "Выводит информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)"
        override fun argParse(logger: Logger, args: Array<String>): Info = Info
    },
    SHOW {
        override val id: String = "show"
        override val help: String = "Выводит все элементы коллекции в строковом представлении"
        override fun argParse(logger: Logger, args: Array<String>): Show = Show
    },
    ;

    /**
     * Уникальный идентификатор команды
     */
    abstract val id: String

    /**
     * Краткая информация о команде
     */
    abstract val help: String

    @Suppress("SpellCheckingInspection")
    /**
     * Универсальный конструктор объекта команды упакованной с аргументами
     * @param logger логгер для вывода сообщений
     * @param args сырые (необработанные) аргументы
     * @return объект команды который можно применить к коллекции
     */
    abstract fun argParse(logger: Logger, args: Array<String>): BoundCommand?

    companion object {
        private val id2obj = RedBlackTreeSetWithKeyAccess<String, CommandMeta> { id }.apply buildMap@{
            @Suppress("RemoveRedundantQualifierName")
            for (obj in CommandMeta.values()) {
                if (!this@buildMap.add(obj)) {
                    throw RuntimeException("Команды `${obj.name}` и `${this@buildMap[obj.id]!!.name}` имеют одинаковый идентификатор")
                }
            }
        }

        /**
         * Возвращает член перечисления с заданным идентификатором или null, если такого не существует
         */
        @JvmStatic
        fun byId(id: String): CommandMeta? = this.id2obj[id]

        /**
         * Возвращает массив всех объявленных команд
         */
        @Suppress("RemoveRedundantQualifierName")
        @JvmStatic
        fun all(): Iterable<CommandMeta> = this.id2obj.values
    }
}