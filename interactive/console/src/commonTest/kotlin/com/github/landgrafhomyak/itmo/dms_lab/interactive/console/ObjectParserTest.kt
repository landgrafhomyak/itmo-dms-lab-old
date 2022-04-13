package com.github.landgrafhomyak.itmo.dms_lab.interactive.console

import com.github.landgrafhomyak.itmo.dms_lab.collections.LinkedQueue
import com.github.landgrafhomyak.itmo.dms_lab.collections.Queue
import com.github.landgrafhomyak.itmo.dms_lab.collections.RedBlackTreeMap
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue


internal class ObjectParserTest {
    private fun assertMap(expected: Map<String, ObjectParser.Value>, actual: Map<String, ObjectParser.Value>) {
        val queue: Queue<Pair<Map<String, ObjectParser.Value>, Map<String, ObjectParser.Value>>> = LinkedQueue()
        queue.push(expected to actual)
        while (queue.isNotEmpty()) {
            val (e, a) = queue.pop()
            for ((k, ev) in e) {
                assertContains(a, k)
                val av = a[k]!!
                when (ev) {
                    is ObjectParser.Value.Compose -> {
                        assertTrue(av is ObjectParser.Value.Compose)
                        queue.push(ev.original to av.original)
                    }
                    is ObjectParser.Value.Simple  -> {
                        assertTrue(av is ObjectParser.Value.Simple)
                        assertEquals(ev.original, av.original)
                    }
                }
            }
            assertEquals(e.size, a.size)
        }
    }

    private fun mapOf(vararg entries: Pair<String, Any>): Map<String, ObjectParser.Value> = RedBlackTreeMap<String, ObjectParser.Value>().apply {
        for ((k, v) in entries) {
            if (k in this)
                throw RuntimeException("Дупликация ключа в конструкторе словаря")

            @Suppress("UNCHECKED_CAST")
            put(
                k,
                when (v) {
                    is String    -> ObjectParser.Value.Simple(v)
                    is Map<*, *> -> ObjectParser.Value.Compose(v as Map<String, ObjectParser.Value>)
                    else         -> throw RuntimeException("Невалидный тип аргумента в конструкторе теста")
                }
            )
        }
    }

    @Test
    fun testEmpty() {
        assertMap(mapOf(), ObjectParser.parse(""))
    }

    @Test
    fun testOneEntry() {
        assertMap(mapOf("key" to "value"), ObjectParser.parse("key=value"))
    }

    @Test
    fun testSomeEntries() {
        assertMap(mapOf("key1" to "value1", "key2" to "value2"), ObjectParser.parse("key1 = value1 key2 = value2"))
    }

    @Test
    fun testEmptyObject() {
        assertMap(mapOf("obj" to mapOf()), ObjectParser.parse("obj={}"))
    }

    @Test
    fun testObject() {
        assertMap(mapOf("obj" to mapOf("key1" to "value1", "key2" to "value2")), ObjectParser.parse("obj={key1=value1 key2=value2}"))
    }

    @Test
    fun testObjectInObject() {
        assertMap(mapOf("obj" to mapOf("key1" to mapOf("key2" to "value2"))), ObjectParser.parse("obj={ key1={ key2=value2 } }"))
    }

    @Test
    fun testManyObjects() {
        assertMap(mapOf("key1" to "value1", "obj1" to mapOf(), "obj2" to mapOf("key2" to "value2"), "key3" to "value3"), ObjectParser.parse("key1=value1 obj1={   } obj2={key2=value2} key3=value3"))
    }

    @Test
    fun testCompositeString() {
        assertMap(mapOf("key" to " space"), ObjectParser.parse("""key=" "space """))
    }

    @Test
    fun testSpecialSymbols() {
        assertMap(mapOf("quote" to "\"", "slash" to "\\"), ObjectParser.parse("""quote="\"" slash="\\" """))
    }

    @Test
    fun testHardCompositeString() {
        assertMap(mapOf("key" to """ space also some sheet  099009 \ " """), ObjectParser.parse("""key=" "space" also some sheet  099009 \\ \" " """))
    }

    @Test
    fun testReal() {
        assertMap(
            mapOf("name" to "name with space", "value" to mapOf("type" to "string", "size" to "10", "array" to "\"01234567\""), "owner" to "admin", "rights" to mapOf("R" to "true", "W" to "false", "X" to "true")),
            ObjectParser.parse("""name="name with space" value={ type=string size=10 array="\"01234567\"" } owner=admin rights={ R=true W=false X=true } """)
        )
    }

    @Test
    fun testUnclosedObject() {
        assertFailsWith(ParseError::class) {
            ObjectParser.parse("obj={")
        }
    }

    @Test
    fun testUnopenedObject() {
        assertFailsWith(ParseError::class) {
            ObjectParser.parse("obj=}")
        }
    }

    @Test
    fun testUnclosedString() {
        assertFailsWith(ParseError::class) {
            ObjectParser.parse("""string=" """)
        }
    }

    @Test
    fun testWrongKey() {
        assertFailsWith(ParseError::class) {
            ObjectParser.parse("<=>")
        }
    }

    @Test
    fun testNoKey() {
        assertFailsWith(ParseError::class) {
            ObjectParser.parse("=value")
        }
    }
    @Test
    fun testUnAssignedKey() {
        assertFailsWith(ParseError::class) {
            ObjectParser.parse("key1=value1 key2")
        }
    }
}

