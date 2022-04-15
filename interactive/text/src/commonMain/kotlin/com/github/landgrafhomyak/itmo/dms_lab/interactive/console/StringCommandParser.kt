package com.github.landgrafhomyak.itmo.dms_lab.interactive.console

import com.github.landgrafhomyak.itmo.dms_lab.Factory
import com.github.landgrafhomyak.itmo.dms_lab.objects.FailedCreateObjectException
import com.github.landgrafhomyak.itmo.dms_lab.requests.Add
import com.github.landgrafhomyak.itmo.dms_lab.requests.AddIfMax
import com.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import com.github.landgrafhomyak.itmo.dms_lab.requests.Clear
import com.github.landgrafhomyak.itmo.dms_lab.requests.Empty
import com.github.landgrafhomyak.itmo.dms_lab.requests.ExecuteScript
import com.github.landgrafhomyak.itmo.dms_lab.requests.Exit
import com.github.landgrafhomyak.itmo.dms_lab.requests.ExternCommand
import com.github.landgrafhomyak.itmo.dms_lab.requests.FilterByDifficulty
import com.github.landgrafhomyak.itmo.dms_lab.requests.Help
import com.github.landgrafhomyak.itmo.dms_lab.requests.History
import com.github.landgrafhomyak.itmo.dms_lab.requests.Info
import com.github.landgrafhomyak.itmo.dms_lab.requests.PrintDescendingByCoordinateX
import com.github.landgrafhomyak.itmo.dms_lab.requests.PrintFieldDescendingMaximumPoint
import com.github.landgrafhomyak.itmo.dms_lab.requests.RemoveById
import com.github.landgrafhomyak.itmo.dms_lab.requests.RemoveGreater
import com.github.landgrafhomyak.itmo.dms_lab.requests.Save
import com.github.landgrafhomyak.itmo.dms_lab.requests.Show
import com.github.landgrafhomyak.itmo.dms_lab.requests.UnexpectedRequestException
import com.github.landgrafhomyak.itmo.dms_lab.requests.Update
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWork
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorkFactoryFromDynamicAndString
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorkId
import com.github.landgrafhomyak.itmo.dms_lab.objects.toLabWorkId
import com.github.landgrafhomyak.itmo.dms_lab.objects.toLabWorkIdOrNull
import com.github.landgrafhomyak.itmo.dms_lab.requests.RequestRejectedException
import kotlin.jvm.JvmStatic

/**
 * Набор функций для разбора строковых команд
 * @param saveClosure замыкание для создания конечного объекта запроса [Save] с указанным путём
 * @param executeClosure замыкание для создания конечного объекта запроса [ExecuteScript] с указанным путём
 */
@Suppress("unused")
class StringCommandParser(
    private val saveClosure: ExternCommand<Save, String>?,
    private val executeClosure: ExternCommand<ExecuteScript, String>?
) {
    /**
     * Парсит строку в [конечный объект запроса][BoundRequest]
     * @throws ParseError если был нарушен синтаксис
     * @throws FailedCreateObjectException если нарушены типы или значения в запросе создания объекта
     * @throws RequestRejectedException если запрос отклонён
     */
    fun parse(string: String): BoundRequest {
        val (requestId, args, argsStart) = split1(string.trim())
        if (requestId.isEmpty()) return Empty

        return when (requestId) {
            Help.id                             -> Help
            Info.id                             -> Info
            Show.id                             -> Show
            Add.id                              -> {
                Add(
                    ParseError.shift(argsStart) {
                        parseLabWork(args ?: throw ParseError(0, "Лабораторная работа не получена"))
                    }
                )
            }
            Update.id                           -> {
                val (ids, labs, labsStart) = split1(args ?: throw ParseError(argsStart, "Идентификатор не был получен"))
                if (labs == null) {
                    throw ParseError(labsStart, "Лабораторная работа не получена")
                }
                Update(
                    ParseError.shift(argsStart) {
                        parseId(ids)
                    },
                    ParseError.shift(labsStart) {
                        parseLabWork(args)
                    }
                )
            }
            RemoveById.id                       -> {
                RemoveById(
                    ParseError.shift(argsStart) {
                        parseId(args ?: throw ParseError(0, "Идентификатор не был получен"))
                    }
                )
            }
            Clear.id                            -> Clear
            Save.id                             -> ParseError.shift(argsStart) {
                this.saveClosure?.build(args ?: throw ParseError(0, "Путь для сохранения не получен")) ?: throw RequestRejectedException("Сохранение не поддерживается")
            }
            ExecuteScript.id                    -> ParseError.shift(argsStart) {
                this.executeClosure?.build(args ?: throw ParseError(0, "Путь для запуска подпрограммы не получен")) ?: throw RequestRejectedException("Запуск подпрограмм не поддерживается")
            }
            Exit.id                             -> Exit
            AddIfMax.id                         -> {
                AddIfMax(
                    ParseError.shift(argsStart) {
                        parseLabWork(args ?: throw ParseError(0, "Лабораторная работа не получена"))
                    }
                )
            }
            RemoveGreater.id                    -> {
                if (args == null) {
                    throw ParseError(argsStart, "Координата для сравнения не получена")
                }
                RemoveGreater(
                    ParseError.shift(argsStart) {
                        args.toLongOrNull() ?: throw ParseError(0, "Координата должна быть целым числом")
                    }
                )
            }
            History.id                          -> History
            FilterByDifficulty.id               -> {
                if (args == null) {
                    throw ParseError(argsStart, "Сложность не была получена")
                }
                FilterByDifficulty(
                    ParseError.shift(argsStart) {
                        LabWorkFactoryFromDynamicAndString.parseDifficulty(args)
                    }
                )

            }
            PrintDescendingByCoordinateX.id     -> PrintDescendingByCoordinateX
            PrintFieldDescendingMaximumPoint.id -> PrintFieldDescendingMaximumPoint
            else                                -> throw UnexpectedRequestException(requestId)
        }
    }

    companion object {
        /**
         * Разделяет строку по пробельному символу и возвращает позицию начала суффикса
         * @return
         * * префикс до первого пробельного символа (может быть пустым)
         * * суффикс после первой последовательности пробельных символов (или `null` если такой нет или является пустой строкой), не может быть пустым
         * * позиция начала суффикса
         */
        // @JvmStatic
        @Suppress("NOTHING_TO_INLINE")
        private inline fun split1(string: String): Triple<String, String?, Int> {
            val endFirst = string.indexOfFirst { c -> c.isWhitespace() }
            if (endFirst == -1)
                return Triple(string, null, string.length)
            for (i in endFirst until string.length) {
                if (!string[i].isWhitespace()) {
                    return Triple(string.slice(0 until endFirst), string.slice(i until string.length), i)
                }
            }
            return Triple(string.slice(0 until endFirst), null, string.length)
        }


        /**
         * Конвертирует строку в [идентификатор лабораторной работы][LabWorkId]
         * @see String.toLabWorkId
         * @see String.toLabWorkIdOrNull
         */
        @JvmStatic
        @Suppress("NOTHING_TO_INLINE")
        fun parseId(raw: String): LabWorkId = raw.toLabWorkIdOrNull() ?: throw ParseError(0, "Не удаётся прочитать идентификатор лабораторной работы")

        /**
         * Парсит лабораторную работу из строки
         */
        @JvmStatic
        @Suppress("MemberVisibilityCanBePrivate")
        fun parseLabWork(raw: String): Factory<LabWork> {
            val obj = ObjectParser.parse(raw)
            return LabWorkFactoryFromDynamicAndString(obj)
        }
    }
}