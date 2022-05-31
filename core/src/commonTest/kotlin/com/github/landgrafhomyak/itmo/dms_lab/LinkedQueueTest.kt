package com.github.landgrafhomyak.itmo.dms_lab

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

/**
 * Набор тестов для проверки [очереди][LinkedQueue].
 */
internal class LinkedQueueTest {
    /**
     * Проверяет, что [очередь][LinkedQueue] корректно [добавляет][LinkedQueue.push] элементы
     */
    @Test
    fun testAdding() {
        buildLinkedQueue<Int> {
            for (v in 0..100)
                push(v)
            assertEquals(101, count())
        }
    }

    /**
     * Проверяет, что [очередь][LinkedQueue] корректно [добавляет][LinkedQueue.push]
     * и [удаляет][LinkedQueue.pop] элементы
     */
    @Test
    fun testAddingAndRemoving() {
        buildLinkedQueue<Int> queue@{
            for (v in 0..100)
                push(v)
            while (isNotEmpty())
                pop()
            assertContentEquals(emptyList(), this@queue)
        }
    }


    /**
     * Проверяет, что [очередь][LinkedQueue] корректно хранит элементы, а [итератор][Iterator]
     * корректно их [возвращает][Iterator.next]
     */
    @Test
    fun testConstIterator() {
        buildLinkedQueue<Int> queue@{
            for (v in 0..100)
                push(v)
            assertContentEquals(0..100, this@queue)
        }
    }

    /**
     * Проверяет, что [итератор][MutableIterator] корректно [удаляет][MutableIterator.remove]
     * элементы из [очереди][LinkedQueue]
     */
    @Test
    fun testMutableIterator() {
        buildLinkedQueue<Int> queue@{
            for (v in 0..100)
                push(v)
            asMutableIterable().removeAll { n -> n % 2 != 0 }
            assertContentEquals(0..100 step 2, this@queue)
        }
    }
}