package com.github.landgrafhomyak.itmo.dms_lab.commands

import com.github.landgrafhomyak.itmo.dms_lab.io.ScriptOutput
import kotlin.jvm.JvmStatic

/**
 * Перечисление всех команд содержащее метаинформацию
 */
enum class CommandMeta {

    HELP {
        override val id: String = "help"
        override val help: String = "Выводит справку по доступным командам"
        override fun argParse(logger: ScriptOutput, args: Array<String>): Help = Help
    },
    INFO {
        override val id: String = "info"
        override val help: String = "Выводит информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)"
        override fun argParse(logger: ScriptOutput, args: Array<String>): Info = Info
    },
    SHOW {
        override val id: String = "show"
        override val help: String = "Выводит все элементы коллекции в строковом представлении"
        override fun argParse(logger: ScriptOutput, args: Array<String>): Show = Show
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
    abstract fun argParse(logger: ScriptOutput, args: Array<String>): BoundCommand?

    companion object {
        @Suppress("RemoveExplicitTypeArguments" /* не работает без явного указания типов шаблона */)
        private val id2obj = buildMap<String, CommandMeta> {
            for (obj in this@Companion.all()) {
                this@buildMap.put(obj.id, obj)?.also { another ->
                    throw RuntimeException("Команды `${obj.name}` и `${another.name}` имеют одинаковый идентификатор")
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
        fun all(): Array<CommandMeta> = CommandMeta.values()
    }
}