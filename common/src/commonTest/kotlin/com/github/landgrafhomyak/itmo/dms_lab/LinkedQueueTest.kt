package com.github.landgrafhomyak.itmo.dms_lab

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

internal class LinkedQueueTest {
    @Test
    fun testAdding() {
        buildQueue<Int> {
            for (v in 0..100)
                push(v)
            assertEquals(101, count())
        }
    }

    @Test
    fun testAddingAndRemoving() {
        buildQueue<Int> {
            for (v in 0..100)
                push(v)
            while (isNotEmpty())
                pop()
        }
    }

    @Test
    fun testConstIterator() {
        buildQueue<Int> queue@{
            for (v in 0..100)
                push(v)
            assertContentEquals(0..100, this@queue)
        }
    }

    @Test
    fun testMutableIterator() {
        buildQueue<Int> queue@{
            for (v in 0..100)
                push(v)
            asMutableIterable().removeAll { n -> n % 2 != 0 }
            assertContentEquals(0..100 step 2, this@queue)
        }
    }
}