package com.github.landgrafhomyak.itmo.dms_lab.interactive.console

import com.github.landgrafhomyak.itmo.dms_lab.collections.LinkedQueue
import com.github.landgrafhomyak.itmo.dms_lab.collections.Queue
import com.github.landgrafhomyak.itmo.dms_lab.objects.LabWorkFactoryFromDynamicAndString
import com.github.landgrafhomyak.itmo.dms_lab.objects.stringMMapOf
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue


/**
 * Набор тестов для [парсера объектов][ObjectParser]
 */
@Suppress("SpellCheckingInspection")
internal class ObjectParserTest {
    /**
     * Рекурсивно сравнивает словари с [упакованными значениями][LabWorkFactoryFromDynamicAndString.Value]
     */
    private fun assertMap(expected: Map<String, LabWorkFactoryFromDynamicAndString.Value>, actual: Map<String, LabWorkFactoryFromDynamicAndString.Value>) {
        val queue: Queue<Pair<Map<String, LabWorkFactoryFromDynamicAndString.Value>, Map<String, LabWorkFactoryFromDynamicAndString.Value>>> = LinkedQueue()
        queue.push(expected to actual)
        while (queue.isNotEmpty()) {
            val (e, a) = queue.pop()
            for ((k, ev) in e) {
                assertContains(a, k)
                val av = a[k]!!
                when (ev) {
                    is LabWorkFactoryFromDynamicAndString.Value.Compose -> {
                        assertTrue(av is LabWorkFactoryFromDynamicAndString.Value.Compose)
                        queue.push(ev.original to av.original)
                    }
                    is LabWorkFactoryFromDynamicAndString.Value.Simple  -> {
                        assertTrue(av is LabWorkFactoryFromDynamicAndString.Value.Simple)
                        assertEquals(ev.original, av.original)
                    }
                }
            }
            assertEquals(e.size, a.size)
        }
    }

    /**
     * Тестирует парсер на наборе данных
     * @param raw строка, которая будет передана в парсер
     * @param entries ожидаемые от словаря данные
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun testOn(
        raw: String,
        vararg entries: Pair<String, Any>
    ) = this.assertMap(stringMMapOf(*entries), ObjectParser.parse(raw))

    /**
     * Проверят что парсер обрабатывает пустые запросы
     */
    @Test
    fun testEmpty() = this.testOn("")

    /**
     * Проверяет, что парсер обрабатывает простое выражение
     */
    @Test
    fun testOneEntry() = this.testOn(
        "key=value",
        "key" to "value"
    )

    /**
     * Проверяет, что парсер может обработать несколько простых значений
     */
    @Test
    fun testSomeEntries() = this.testOn(
        "key1 = value1 key2 = value2",
        "key1" to "value1", "key2" to "value2"
    )

    /**
     * Проверяет, что парсер может обработать пустой составной объект
     */
    @Test
    fun testEmptyObject() = this.testOn(
        "obj={}",
        "obj" to stringMMapOf()
    )

    /**
     * Проверяет, что парсер может обработать не пустой составной объект
     */
    @Test
    fun testObject() = this.testOn(
        "obj={key1=value1 key2=value2}",
        "obj" to stringMMapOf("key1" to "value1", "key2" to "value2")
    )

    /**
     * Проверяет, что парсер может обработать вложенные составные объекты
     */
    @Test
    fun testObjectInObject() = this.testOn(
        "obj={ key1={ key2=value2 } }",
        "obj" to stringMMapOf("key1" to stringMMapOf("key2" to "value2"))
    )

    /**
     * Проверяет, что парсер может обработать несколько последовательных составных объектов
     */
    @Test
    fun testManyObjects() = this.testOn(
        "key1=value1 obj1={   } obj2={key2=value2} key3=value3",
        "key1" to "value1", "obj1" to stringMMapOf(), "obj2" to stringMMapOf("key2" to "value2"), "key3" to "value3"
    )

    /**
     * Проверяет, что парсер может обработать строку с экранизацией
     */
    @Test
    fun testCompositeString() = this.testOn(
        """key=" "space """,
        "key" to " space"
    )

    /**
     * Проверяет, что парсер может обработать строку со спец. символами
     */
    @Test
    fun testSpecialSymbols() = this.testOn(
        """quote="\"" slash="\\" """,
        "quote" to "\"", "slash" to "\\"
    )

    /**
     * Проверяет, что парсер может обработать сложную составную строку
     */
    @Test
    fun testHardCompositeString() = this.testOn(
        """key=" "space" also some sheet  099009 \\ \" " """,
        "key" to """ space also some sheet  099009 \ " """
    )

    /**
     * Стресс-тест
     */
    @Test
    fun testReal() = this.testOn(
        """name="name with space" value={ type=string size=10 array="\"01234567\"" } owner=admin rights={ R=true W=false X=true } """,
        "name" to "name with space", "value" to stringMMapOf("type" to "string", "size" to "10", "array" to "\"01234567\""), "owner" to "admin", "rights" to stringMMapOf("R" to "true", "W" to "false", "X" to "true")
    )


    /**
     * Проверяет, что незакрытый объект является синтаксической ошибкой
     */
    @Test
    fun testUnclosedObject() {
        assertFailsWith(ParseError::class) {
            ObjectParser.parse("obj={")
        }
    }


    /**
     * Проверяет, что неоткрытый объект является синтаксической ошибкой
     */
    @Test
    fun testUnopenedObject() {
        assertFailsWith(ParseError::class) {
            ObjectParser.parse("obj=}")
        }
    }


    /**
     * Проверяет, что незакрытая строка является синтаксической ошибкой
     */
    @Test
    fun testUnclosedString() {
        assertFailsWith(ParseError::class) {
            ObjectParser.parse("""string=" """)
        }
    }


    /**
     * Проверяет, что незакрытый объект является синтаксической ошибкой
     */
    @Test
    fun testWrongKey() {
        assertFailsWith(ParseError::class) {
            ObjectParser.parse("<=>")
        }
    }


    /**
     * Проверяет, что отстутствие ключа является синтаксической ошибкой
     */
    @Test
    fun testNoKey() {
        assertFailsWith(ParseError::class) {
            ObjectParser.parse("=value")
        }
    }

    /**
     * Проверяет, что отстутствие значения является синтаксической ошибкой
     */
    @Test
    fun testUnAssignedKey() {
        assertFailsWith(ParseError::class) {
            ObjectParser.parse("key1=value1 key2")
        }
    }

    /**
     * Проверяет, что дупликация ключа является синтаксической ошибкой
     */
    @Test
    fun testDuplicatedKey() {
        assertFailsWith(ParseError::class) {
            ObjectParser.parse("key=value key=value")
        }
    }
}

