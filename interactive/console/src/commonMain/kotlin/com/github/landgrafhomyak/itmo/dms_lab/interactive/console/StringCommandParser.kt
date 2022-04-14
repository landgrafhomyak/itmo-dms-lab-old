package com.github.landgrafhomyak.itmo.dms_lab.interactive.console

import com.github.landgrafhomyak.itmo.dms_lab.commands.Add
import com.github.landgrafhomyak.itmo.dms_lab.commands.AddIfMax
import com.github.landgrafhomyak.itmo.dms_lab.commands.BoundRequest
import com.github.landgrafhomyak.itmo.dms_lab.commands.Clear
import com.github.landgrafhomyak.itmo.dms_lab.commands.Empty
import com.github.landgrafhomyak.itmo.dms_lab.commands.ExecuteScript
import com.github.landgrafhomyak.itmo.dms_lab.commands.Exit
import com.github.landgrafhomyak.itmo.dms_lab.commands.FilterByDifficulty
import com.github.landgrafhomyak.itmo.dms_lab.commands.Help
import com.github.landgrafhomyak.itmo.dms_lab.commands.History
import com.github.landgrafhomyak.itmo.dms_lab.commands.Info
import com.github.landgrafhomyak.itmo.dms_lab.commands.PrintDescendingByCoordinateX
import com.github.landgrafhomyak.itmo.dms_lab.commands.PrintFieldDescendingMaximumPoint
import com.github.landgrafhomyak.itmo.dms_lab.commands.RemoveById
import com.github.landgrafhomyak.itmo.dms_lab.commands.RemoveGreater
import com.github.landgrafhomyak.itmo.dms_lab.commands.Save
import com.github.landgrafhomyak.itmo.dms_lab.commands.Show
import com.github.landgrafhomyak.itmo.dms_lab.commands.UnexpectedRequestException
import com.github.landgrafhomyak.itmo.dms_lab.commands.Update
import com.github.landgrafhomyak.itmo.dms_lab.objects.Coordinates
import com.github.landgrafhomyak.itmo.dms_lab.objects.Country
import com.github.landgrafhomyak.itmo.dms_lab.objects.Difficulty
import com.github.landgrafhomyak.itmo.dms_lab.objects.EyeColor
import com.github.landgrafhomyak.itmo.dms_lab.objects.FailedCreateObjectException
import com.github.landgrafhomyak.itmo.dms_lab.objects.HairColor
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWork
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorkId
import com.github.landgrafhomyak.itmo.dms_lab.objects.Person
import com.github.landgrafhomyak.itmo.dms_lab.objects.toLabWorkIdOrNull
import kotlin.jvm.JvmStatic

@Suppress("unused")
object StringCommandParser {
    @Suppress("unused")
    @JvmStatic
    fun parse(string: String): BoundRequest {
        val (requestId, args, argsStart) = this.split1(string.trim())
        if (requestId.isEmpty()) return Empty

        return when (requestId) {
            Help.id                             -> Help
            Info.id                             -> Info
            Show.id                             -> Show
            Add.id                              -> {
                Add(
                    ParseError.shift(argsStart) {
                        this.parseLabWork(args ?: throw ParseError(0, "Лабораторная работа не получена"))
                    }
                )
            }
            Update.id                           -> {
                val (ids, labs, labsStart) = this.split1(args ?: throw ParseError(argsStart, "Идентификатор не был получен"))
                if (labs == null) {
                    throw ParseError(labsStart, "Лабораторная работа не получена")
                }
                Update(
                    ParseError.shift(argsStart) {
                        this.parseId(ids)
                    },
                    ParseError.shift(labsStart) {
                        this.parseLabWork(args)
                    }
                )
            }
            RemoveById.id                       -> {
                RemoveById(
                    ParseError.shift(argsStart) {
                        this.parseId(args ?: throw ParseError(0, "Идентификатор не был получен"))
                    }
                )
            }
            Clear.id                            -> Clear
            Save.id                             -> TODO()
            ExecuteScript.id                    -> TODO()
            Exit.id                             -> Exit
            AddIfMax.id                         -> {
                AddIfMax(
                    ParseError.shift(argsStart) {
                        this.parseLabWork(args ?: throw ParseError(0, "Лабораторная работа не получена"))
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
                        this.parseDifficulty(args)
                    }
                )

            }
            PrintDescendingByCoordinateX.id     -> PrintDescendingByCoordinateX
            PrintFieldDescendingMaximumPoint.id -> PrintFieldDescendingMaximumPoint
            else                                -> throw UnexpectedRequestException(requestId)
        }
    }

    @JvmStatic
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

    @JvmStatic
    @Suppress("NOTHING_TO_INLINE")
    fun parseId(raw: String): LabWorkId = raw.toLabWorkIdOrNull() ?: throw ParseError(0, "Не удаётся прочитать идентификатор лабораторной работы")

    /**
     * Сокращение для получения примитивных данных в [StringCommandParser.parseLabWork]
     */
    @JvmStatic
    @Suppress("NOTHING_TO_INLINE")
    private inline fun getString(map: Map<String, ObjectParser.Value>, key: String, message: String): String =
        (map[key] as? ObjectParser.Value.Simple)?.original ?: throw ParseError(null, message)

    /**
     * Сокращение для получения составного объекта в [StringCommandParser.parseLabWork]
     */
    @JvmStatic
    @Suppress("NOTHING_TO_INLINE")
    private inline fun getObject(map: Map<String, ObjectParser.Value>, key: String, message: String): Map<String, ObjectParser.Value> =
        (map[key] as? ObjectParser.Value.Compose)?.original ?: throw ParseError(null, message)


    /**
     * Парсит лабораторную работу из строки
     */
    @JvmStatic
    @Suppress("MemberVisibilityCanBePrivate")
    fun parseLabWork(raw: String): LabWork {
        val obj = ObjectParser.parse(raw)
        return LabWork(
            this.getString(obj, "name", "Имя лабораторной работы не было получено"),

            this.getObject(obj, "coordinates", "Координаты не были получены").let { obj2 ->
                Coordinates(
                    this.getString(obj2, "x", "Координата X не была получена")
                        .toLongOrNull() ?: throw ParseError(null, "Координата X должна быть целым числом"),
                    this.getString(obj2, "y", "Координата Y не была получена")
                        .toLongOrNull() ?: throw ParseError(null, "Координата Y должна быть целым числом")
                )
            },

            this.getString(obj, "minimal_point", "Минимальная точка не была получена")
                .toLongOrNull() ?: throw ParseError(null, "Минимальная точка должна быть целым числом"),

            this.getString(obj, "maximum_point", "Максимальная точка не была получена")
                .toDoubleOrNull() ?: throw ParseError(null, "Максимальная точка должна быть вещественным числом"),

            this.getString(obj, "personal_qualities_maximum", "Максимальный показатель личных качеств не были получена")
                .toIntOrNull() ?: throw ParseError(null, "Максимальный показатель личных качеств быть целым числом"),

            this.parseDifficulty(this.getString(obj, "difficulty", "Сложность лабораторной работы не была получена")),

            this.getObject(obj, "author", "Автор не был получен").let { obj2 ->
                Person(
                    this.getString(obj2, "name", "Имя автора не было получено"),
                    this.getString(obj2, "weight", "Вес автора")
                        .toFloatOrNull() ?: throw ParseError(null, "Вес автора должен быть вещественным числом"),

                    when (this.getString(obj2, "eye_color", "Цвет глаз автора не был получен")) {
                        "BLACK"  -> EyeColor.BLACK
                        "BLUE"   -> EyeColor.BLUE
                        "GREEN"  -> EyeColor.GREEN
                        "YELLOW" -> EyeColor.YELLOW
                        "WHITE"  -> EyeColor.WHITE
                        else     -> throw FailedCreateObjectException("Неизвестный цвет глаз")
                    },

                    when (this.getString(obj2, "hair_color", "Цвет волос автора не был получен")) {
                        "BROWN" -> HairColor.BROWN
                        "RED"   -> HairColor.RED
                        "WHITE" -> HairColor.WHITE
                        else    -> throw FailedCreateObjectException("Неизвестный цвет волос")
                    },
                    when (this.getString(obj2, "nationality", "Национальность автора не была получен")) {
                        "CHINA"       -> Country.CHINA
                        "GERMANY"     -> Country.GERMANY
                        "JAPAN"       -> Country.JAPAN
                        "RUSSIA"      -> Country.RUSSIA
                        "NORTH_KOREA" -> Country.NORTH_KOREA
                        else          -> throw FailedCreateObjectException("Неизвестная национальность")
                    }
                )
            }
        )
    }

    @JvmStatic
    @Suppress("NOTHING_TO_INLINE")
    private inline fun parseDifficulty(raw: String): Difficulty = when (raw) {
        "HARD"     -> Difficulty.HARD
        "INSANE"   -> Difficulty.INSANE
        "TERRIBLE" -> Difficulty.TERRIBLE
        else       -> throw FailedCreateObjectException("Неизвестная сложность")
    }

}