package io.github.landgrafhomyak.itmo.dms_lab.interop

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


/**
 * Набор тестов для [парсера объектов][InLineObjectDecoder]
 */
@Suppress("SpellCheckingInspection")
internal class InLineObjectDecoderTest {
    /**
     * Тестирует парсер на наборе данных
     * @param raw строка, которая будет передана в парсер
     */
    private inline fun <T> testOn(
        raw: String,
        serializer: KSerializer<T>,
        expected: T
    ) {
        assertEquals(expected, serializer.deserialize(InLineObjectDecoder(raw)))
    }

    @Serializable
    object EmptyObj

    /**
     * Тестирует парсер на пустом запросы
     */
    @Test
    fun testEmpty() = this.testOn("", EmptyObj.serializer(), EmptyObj)


    @Serializable
    private data class Pair1(val key1: String)

    /**
     * Тестирует парсер на простом выражении
     */
    @Test
    fun testOneEntry() = this.testOn(
        "key1=value",
        Pair1.serializer(),
        Pair1(key1 = "value")
    )


    @Serializable
    private data class Pair2(val key1: String, val key2: String)


    /**
     * Тестирует парсер на запросе из нескольких простых значений
     */
    @Test
    fun testSomeEntries() = this.testOn(
        "key1 = value1 key2 = value2",
        Pair2.serializer(),
        Pair2(key1 = "value1", key2 = "value2")
    )


    @Serializable
    private data class Pair1Obj0(val obj: EmptyObj)


    /**
     * Тестирует парсер на запросе с пустым составным объектом
     */
    @Test
    fun testEmptyObject() = this.testOn(
        "obj={}",
        Pair1Obj0.serializer(),
        Pair1Obj0(obj = EmptyObj)
    )

    @Serializable
    private data class Pair1Obj2(val obj: Pair2)

    /**
     * Тестирует парсер на запросе со вложенным составным объектом
     */
    @Test
    fun testObject() = this.testOn(
        "obj={key1=value1 key2=value2}",
        Pair1Obj2.serializer(),
        Pair1Obj2(obj = Pair2(key1 = "value1", key2 = "value2"))
    )

    @Serializable
    private data class Pair1Obj1(val key2: Pair1)

    @Serializable
    private data class Pair1Obj1Obj1(val obj: Pair1Obj1)


    /**
     * Тестирует парсер на запросе с множественными вложенными составными объектами
     */
    @Test
    fun testObjectInObject() = this.testOn(
        "obj={ key2={ key1=value2 } }",
        Pair1Obj1Obj1.serializer(),
        Pair1Obj1Obj1(obj = Pair1Obj1(key2 = Pair1(key1 = "value2")))
    )


    @Serializable
    private data class MediumObject(val key2: String, val obj1: EmptyObj, val obj2: Pair1, val key3: String)


    /**
     * Тестирует парсер на запросе из нескольких последовательных составных объектов
     */
    @Test
    fun testManyObjects() = this.testOn(
        "key2=value1 obj1={   } obj2={key1=value2} key3=value3",
        MediumObject.serializer(),
        MediumObject(key2 = "value1", obj1 = EmptyObj, obj2 = Pair1(key1 = "value2"), key3 = "value3")
    )

    /**
     * Тестирует парсер на запросе с экранизированной строкой
     */
    @Test
    fun testCompositeString() = this.testOn(
        """key1=" "space """,
        Pair1.serializer(),
        Pair1(key1 = " space")
    )

    @Serializable
    private data class QuoteSlash(val quote: String, val slash: String)

    /**
     * Тестирует парсер на запросе со спец. символами
     */
    @Test
    fun testSpecialSymbols() = this.testOn(
        """quote="\"" slash="\\" """,
        QuoteSlash.serializer(),
        QuoteSlash(quote = "\"", slash = "\\")
    )

    /**
     * Тестирует парсер на запросе со сложной составной строкой
     */
    @Test
    fun testHardCompositeString() = this.testOn(
        """key1=" "space" also some sheet  099009 \\ \" " """,
        Pair1.serializer(),
        Pair1(key1 = """ space also some sheet  099009 \ " """)
    )

    @Serializable
    private data class StressTest(val name: String, val value: Value, val owner: String, val rights: Rights) {
        @Serializable
        data class Value(val type: String, val size: Int, val array: String)

        @Serializable
        data class Rights(val R: Boolean, val W: Boolean, val X: Boolean)
    }

    /**
     * Стресс-тест
     */
    @Test
    fun testReal() = this.testOn(
        """name="name with space" value={ type=string size=10 array="\"01234567\"" } owner=admin rights={ R=true W=false X=true } """,
        StressTest.serializer(),
        StressTest(
            name = "name with space",
            value = StressTest.Value(type = "string", size = 10, array = "\"01234567\""),
            owner = "admin",
            rights = StressTest.Rights(R = true, W = false, X = true)
        )
    )


    /**
     * Тестирует парсер на запросе с незакрытым объектом (синтаксическая ошибка)
     */
    @Test
    fun testUnclosedObject() {
        assertFailsWith(SerializationException::class) {
            this@InLineObjectDecoderTest.testOn(
                "obj={",
                Pair1Obj0.serializer(),
                Pair1Obj0(EmptyObj)
            )
        }
    }


    /**
     * Тестирует парсер на запросе с неоткрытым объектом (синтаксическая ошибка)
     */
    @Test
    fun testUnopenedObject() {
        assertFailsWith(SerializationException::class) {
            this@InLineObjectDecoderTest.testOn(
                "obj=}",
                Pair1Obj0.serializer(),
                Pair1Obj0(EmptyObj)
            )
        }
    }


    /**
     * Тестирует парсер на запросе с незакрытой строкой (синтаксическая ошибка)
     */
    @Test
    fun testUnclosedString() {
        assertFailsWith(SerializationException::class) {
            this@InLineObjectDecoderTest.testOn(
                "key1=\" ",
                Pair1.serializer(),
                Pair1("")
            )
        }
    }


    /**
     * Тестирует парсер на запрос с неправильными названиями ключей
     */
    @Test
    fun testWrongKey() {
        assertFailsWith(SerializationException::class) {
            this@InLineObjectDecoderTest.testOn(
                "invalid_key=value",
                Pair1.serializer(),
                Pair1("")
            )
        }
    }


    /**
     * Проверяет, что отстутствие ключа является синтаксической ошибкой
     */
    @Test
    fun testNoKey() {
        assertFailsWith(SerializationException::class) {
            this@InLineObjectDecoderTest.testOn(
                "=value",
                Pair1.serializer(),
                Pair1("")
            )
        }
    }

    /**
     * Тестирует парсер на запросе с ключём без значения (синтаксическая ошибка)
     */
    @Test
    fun testUnAssignedKey() {
        assertFailsWith(SerializationException::class) {
            this@InLineObjectDecoderTest.testOn(
                "=value",
                Pair1.serializer(),
                Pair1("")
            )
        }
    }
}

