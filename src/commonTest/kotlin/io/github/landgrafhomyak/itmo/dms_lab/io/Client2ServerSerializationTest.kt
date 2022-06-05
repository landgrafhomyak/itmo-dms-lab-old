@file:OptIn(ExperimentalUnsignedTypes::class)

package io.github.landgrafhomyak.itmo.dms_lab.io


import kotlinx.serialization.KSerializer
import kotlin.test.Test
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlin.jvm.JvmInline
import kotlin.test.assertEquals

/**
 * Тестирует кодировку данный при передаче от сервера клиенту
 * @see Client2ServerEncoder
 * @see Client2ServerDecoder
 */
@Suppress("unused")
internal class Client2ServerSerializationTest {
    private inline fun <reified T> test(serializer: KSerializer<T>, original: T) = serializer.run {
        val buffer = mutableListOf<UByteArray>()
        this.serialize(Client2ServerEncoder(buffer), original)
        val encoded = buffer.flatten().toUByteArray()
        val decoded = this.deserialize(Client2ServerDecoder(encoded))
        assertEquals(original, decoded)
        return@run
    }

    /**
     * @see Client2ServerSerializationTest.testSimpleStruct
     */
    @Serializable
    private data class SimpleStruct(val a: Int, val b: String)

    /**
     * Тестирует передачу [структуры с примитивными полями][Client2ServerSerializationTest.SimpleStruct]
     */
    @Test
    fun testSimpleStruct() = this.test(
        SimpleStruct.serializer(), SimpleStruct(1, "1234")
    )

    /**
     * @see Client2ServerSerializationTest.testStructWithEnum
     */
    @Serializable
    private enum class SimpleEnum {
        ABC,
        DEF,
        HIJ
    }

    /**
     * @see Client2ServerSerializationTest.testStructWithEnum
     */
    @Serializable
    private data class StructWithEnum(val e: SimpleEnum)

    /**
     * Тестирует передачу [структуры][Client2ServerSerializationTest.StructWithEnum] содержащей [перечисление][Client2ServerSerializationTest.SimpleEnum]
     */
    @Test
    fun testStructWithEnum() = this.test(
        StructWithEnum.serializer(), StructWithEnum(SimpleEnum.DEF)
    )

    /**
     * @see Client2ServerSerializationTest.testStructInStruct
     */
    @Serializable
    private data class StructInStruct(val s: SimpleStruct)

    /**
     * Тестирует передачу [структуры со сложными полями][Client2ServerSerializationTest.StructInStruct]
     */
    @Test
    fun testStructInStruct() = this.test(
        StructInStruct.serializer(),
        StructInStruct(SimpleStruct(123421, "fkmo2-f'kvm[i2n-en h uhuah980-91-=1 =a"))
    )

    /**
     * @see Client2ServerSerializationTest.testInlineStruct
     */
    @Serializable
    @JvmInline
    private value class InlineStruct(val s: SimpleStruct)

    /**
     * Тестирует передачу [inline](https://kotlinlang.org/docs/inline-classes.html) [структуры][Client2ServerSerializationTest.InlineStruct]
     */
    @Test
    fun testInlineStruct() = this.test(
        InlineStruct.serializer(),
        InlineStruct(SimpleStruct(123421, "fkmo2-f'kvm[i2n-en h uhuah980-91-=1 =a"))
    )

    /**
     * Тестирует передачу [коллекций][Collection] (напр. [списков][List])
     */
    @Test
    fun testList() = this.test(
        ListSerializer(SimpleStruct.serializer()),
        List(37) { i -> SimpleStruct(i * 67 - 92, "{$i}") }
    )


    /**
     * @see Client2ServerSerializationTest.testHardStruct
     */
    @Serializable
    private data class HardStruct(val s: SimpleStruct, val e: StructWithEnum, val i: InlineStruct, val c: List<SimpleEnum>)

    /**
     * Тестирует передачу [структуры][Client2ServerSerializationTest.HardStruct] содержащую все сложные типы
     */
    @Test
    fun testHardStruct() = this.test(
        HardStruct.serializer(),
        HardStruct(
            SimpleStruct(401, "f867huoij oigyuhbjnm,. ["),
            StructWithEnum(SimpleEnum.HIJ),
            InlineStruct(SimpleStruct(18, ",kmonjibyuvtr87654sdw rdfcg")),
            List(74) { i -> SimpleEnum.values()[(i * 48 + 11) % 3] }
        )
    )

    /**
     * @see Client2ServerSerializationTest.testNullableNull
     * @see Client2ServerSerializationTest.testNullableNotNull
     */
    @Serializable
    private data class NullableStruct(val n: Int?)

    /**
     * Тестирует передачу [структуры][Client2ServerSerializationTest.NullableStruct] содержащуей `null`
     */
    @Test
    fun testNullableNull() = this.test(
        NullableStruct.serializer(),
        NullableStruct(null)
    )

    /**
     * Тестирует передачу [структуры][Client2ServerSerializationTest.NullableStruct] содержащей nullable-тип
     */
    @Test
    fun testNullableNotNull() = this.test(
        NullableStruct.serializer(),
        NullableStruct(13)
    )
}