package com.github.landgrafhomyak.itmo.dms_lab.interactive.console

import com.github.landgrafhomyak.itmo.dms_lab.MirrorFactory
import com.github.landgrafhomyak.itmo.dms_lab.requests.Add
import com.github.landgrafhomyak.itmo.dms_lab.requests.BoundRequest
import com.github.landgrafhomyak.itmo.dms_lab.requests.Empty
import com.github.landgrafhomyak.itmo.dms_lab.requests.FilterByDifficulty
import com.github.landgrafhomyak.itmo.dms_lab.requests.Help
import com.github.landgrafhomyak.itmo.dms_lab.requests.RemoveById
import com.github.landgrafhomyak.itmo.dms_lab.requests.Show
import com.github.landgrafhomyak.itmo.dms_lab.requests.UnexpectedRequestException
import com.github.landgrafhomyak.itmo.dms_lab.objects.Coordinates
import com.github.landgrafhomyak.itmo.dms_lab.objects.Country
import com.github.landgrafhomyak.itmo.dms_lab.objects.Difficulty
import com.github.landgrafhomyak.itmo.dms_lab.objects.EyeColor
import com.github.landgrafhomyak.itmo.dms_lab.objects.FailedCreateObjectException
import com.github.landgrafhomyak.itmo.dms_lab.objects.HairColor
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWork
import com.github.landgrafhomyak.itmo.dms_lab.objects.Person
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

/**
 * Тестирует [парсер строковых запросов][StringCommandParser]
 */
@Suppress("SpellCheckingInspection")
internal class StringCommandParserTest {
    /**
     * Тестирует парсер на наборе данных
     * @param raw строка, которая будет передана в парсер
     * @param commands ожидаемые от словаря данные
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun testOn(raw: String, vararg commands: BoundRequest) {
        assertContentEquals(commands.asIterable(), raw.split("\n").map(NoOpStringCommandParser::parse))
    }

    /**
     * Проверяет, что парсер принимает пустой запрос
     */
    @Test
    fun testEmpty() = this.testOn(
        "",
        Empty
    )

    /**
     * Проверяет, что парсер принимает простые команды
     */
    @Test
    fun testSimpleCommands() = this.testOn(
        "help\nshow",
        Help,
        Show
    )


    /**
     * Проверяет, что парсер сообщает об неизвестных командах
     */
    @Test
    fun testUnExistedRequest() {
        assertFailsWith(UnexpectedRequestException::class) {
            NoOpStringCommandParser.parse("javascript_the_best")
        }
    }


    /**
     * Проверяет, что парсер принимает примитивные типы
     */
    @Test
    fun testWithPrimitiveArgInt() = this.testOn(
        "remove_by_id 123",
        RemoveById(123)
    )


    /**
     * Проверяет, что парсер принимает значения перечислений
     */
    @Test
    fun testWithPrimitiveArgEnum() = this.testOn(
        "filter_by_difficulty TERRIBLE",
        FilterByDifficulty(Difficulty.TERRIBLE)
    )


    /**
     * Проверяет, что парсер принимает полную [лабораторную работу][LabWork]
     */
    @Test
    fun testWithLabWork() = this.testOn(
        "add name=test coordinates={ x=0 y=0 } minimal_point=10 maximum_point=10.0 personal_qualities_maximum=228 difficulty=TERRIBLE author={ name=admin weight=62.0 eye_color=BLUE hair_color=BROWN nationality=RUSSIA }",
        Add(MirrorFactory(LabWork("test", Coordinates(0, 0), 10, 10.0, 228, Difficulty.TERRIBLE, Person("admin", 62.0f, EyeColor.BLUE, HairColor.BROWN, Country.RUSSIA))))
    )

    /**
     * Проверяет, что парсер не даёт пропустить какие-то поля в [лабораторной работе][LabWork]
     */
    @Test
    fun testMissedSmthInLabWork() {
        assertFailsWith(FailedCreateObjectException::class) {
            NoOpStringCommandParser.parse("add name=test coordinates={ x=0 y=0 } minimal_point=10 maximum_point=10.0 difficulty=TERRIBLE author={ name=admin weight=62.0 eye_color=BLUE hair_color=BROWN nationality=RUSSIA }")
        }
        assertFailsWith(FailedCreateObjectException::class) {
            NoOpStringCommandParser.parse("add name=test coordinates=\"(0; 0)\" minimal_point=10 maximum_point=10.0 personal_qualities_maximum=228 difficulty=TERRIBLE author={ name=admin weight=62.0 eye_color=BLUE hair_color=BROWN nationality=RUSSIA }")
        }
    }


    /**
     * Проверяет, что парсер не принимает несуществующие значения перечислений
     */
    @Test
    fun testInvalidEnumValue() {
        assertFailsWith(FailedCreateObjectException::class) {
            NoOpStringCommandParser.parse("add name=test coordinates={ x=0 y=0 } minimal_point=10 maximum_point=10.0 personal_qualities_maximum=228 difficulty=SO_EASY author={ name=admin weight=62.0 eye_color=BLUE hair_color=BROWN nationality=RUSSIA }")
        }
    }

    /**
     * Проверяет, что парсер не даёт пропускать аргументы
     */
    @Test
    fun testMissedArg() {
        assertFailsWith(ParseError::class) {
            NoOpStringCommandParser.parse("remove_by_id")
        }
    }
}