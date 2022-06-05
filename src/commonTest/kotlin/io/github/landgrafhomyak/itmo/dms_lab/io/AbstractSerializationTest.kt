@file:OptIn(ExperimentalUnsignedTypes::class)

package io.github.landgrafhomyak.itmo.dms_lab.io


import kotlinx.serialization.KSerializer
import kotlin.test.Test
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.jvm.JvmInline
import kotlin.test.assertEquals

/**
 * Тестирует пару [энкодер][Encoder] -> [декодер][Decoder]
 */
@Suppress("unused", "SpellCheckingInspection")
abstract class AbstractSerializationTest {
    private inline fun <reified T> test(serializer: KSerializer<T>, original: T) = serializer.run {
        val buffer = mutableListOf<UByteArray>()
        this.serialize(this@AbstractSerializationTest.createEncoder(buffer), original)
        val encoded = buffer.flatten().toUByteArray()
        val decoded = this.deserialize(this@AbstractSerializationTest.createDecoder(encoded))
        assertEquals(original, decoded)
        return@run
    }

    /**
     * Фабрика [энкодера][Encoder]
     */
    protected abstract fun createEncoder(buffer: MutableList<UByteArray>): Encoder


    /**
     * Фабрика [декодера][Encoder]
     */
    protected abstract fun createDecoder(raw: UByteArray): Decoder

    /**
     * @see AbstractSerializationTest.testSimpleStruct
     */
    @Serializable
    private data class SimpleStruct(val a: Int, val b: String)

    /**
     * Тестирует передачу [структуры с примитивными полями][AbstractSerializationTest.SimpleStruct]
     */
    @Test
    fun testSimpleStruct() = this.test(
        SimpleStruct.serializer(), SimpleStruct(1, "1234")
    )

    /**
     * @see AbstractSerializationTest.testStructWithEnum
     */
    @Serializable
    private enum class SimpleEnum {
        ABC,
        DEF,
        HIJ
    }

    /**
     * @see AbstractSerializationTest.testStructWithEnum
     */
    @Serializable
    private data class StructWithEnum(val e: SimpleEnum)

    /**
     * Тестирует передачу [структуры][AbstractSerializationTest.StructWithEnum] содержащей [перечисление][AbstractSerializationTest.SimpleEnum]
     */
    @Test
    fun testStructWithEnum() = this.test(
        StructWithEnum.serializer(), StructWithEnum(SimpleEnum.DEF)
    )

    /**
     * @see AbstractSerializationTest.testStructInStruct
     */
    @Serializable
    private data class StructInStruct(val s: SimpleStruct)

    /**
     * Тестирует передачу [структуры со сложными полями][AbstractSerializationTest.StructInStruct]
     */
    @Test
    fun testStructInStruct() = this.test(
        StructInStruct.serializer(),
        StructInStruct(SimpleStruct(123421, "fkmo2-f'kvm[i2n-en h uhuah980-91-=1 =a"))
    )

    /**
     * @see AbstractSerializationTest.testInlineStruct
     */
    @Serializable
    @JvmInline
    private value class InlineStruct(val s: SimpleStruct)

    /**
     * Тестирует передачу [inline](https://kotlinlang.org/docs/inline-classes.html) [структуры][AbstractSerializationTest.InlineStruct]
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
     * @see AbstractSerializationTest.testHardStruct
     */
    @Serializable
    private data class HardStruct(val s: SimpleStruct, val e: StructWithEnum, val i: InlineStruct, val c: List<SimpleEnum>)

    /**
     * Тестирует передачу [структуры][AbstractSerializationTest.HardStruct] содержащую все сложные типы
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
     * @see AbstractSerializationTest.testNullableNull
     * @see AbstractSerializationTest.testNullableNotNull
     */
    @Serializable
    private data class NullableStruct(val n: Int?)

    /**
     * Тестирует передачу [структуры][AbstractSerializationTest.NullableStruct] содержащуей `null`
     */
    @Test
    fun testNullableNull() = this.test(
        NullableStruct.serializer(),
        NullableStruct(null)
    )

    /**
     * Тестирует передачу [структуры][AbstractSerializationTest.NullableStruct] содержащей nullable-тип
     */
    @Test
    fun testNullableNotNull() = this.test(
        NullableStruct.serializer(),
        NullableStruct(13)
    )
}