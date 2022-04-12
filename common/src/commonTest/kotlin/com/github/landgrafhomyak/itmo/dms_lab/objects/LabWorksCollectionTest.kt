package com.github.landgrafhomyak.itmo.dms_lab.objects

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Тестирование методов [коллекции лабораторных работ][LabWorksCollection]
 */
internal class LabWorksCollectionTest {
    /**
     * Создаёт коллекцию, добавляет в неё стартовые элементы и применяет к нему функцию (нужно исключительно для красоты)
     */
    private fun collection(vararg works: LabWork, block: LabWorksCollection.(Array<out LabWork>) -> Unit) = LabWorksCollection().apply {
        for (work in works)
            this.add(work)
    }.run { this@run.block(works) }

    /**
     * Проверяет что коллекция содержит в себе добавленные элементы
     */
    @Test
    fun testHolding() = this.collection(
        LabWork("231", Coordinates(15, 15), 50, 90.0, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA))
    ) {
        assertEquals(1, this@collection.size)
    }

    /**
     * Проверяет что коллекция может удалять элементы
     */
    @Test
    fun testDeleting() = this.collection(
        LabWork("231", Coordinates(15, 15), 50, 90.0, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)),
        LabWork("231", Coordinates(15, 15), 50, 90.0, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)),
        LabWork("231", Coordinates(15, 15), 50, 90.0, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)),
    ) {
        assertEquals(3, this@collection.size)
        this@collection.remove(this@collection.iterator().next().id!!)
        assertEquals(2, this@collection.size)
        this@collection.remove(this@collection.iterator().next().id!!)
        assertEquals(1, this@collection.size)
        this@collection.remove(this@collection.iterator().next().id!!)
        assertTrue(this@collection.isEmpty())
    }

    /**
     * Проверяет что выдача и обращение по [идентификатору][LabWork.id] работает корректно
     */
    @Test
    fun testAccessById() = this.collection {
        val works = arrayOf(
            LabWork("231", Coordinates(15, 15), 50, 90.0, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)),
            LabWork("231", Coordinates(15, 15), 50, 90.0, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)),
            LabWork("231", Coordinates(15, 15), 50, 90.0, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA))
        )
        val id2obj = buildMap map@{
            for (work in works) {
                val id = this@collection.add(work)
                assertTrue(id !in this@map)
                this@map[id] = work
            }
        }

        for (id in id2obj.keys)
            assertSame(id2obj[id], this@collection[id])
    }

    /**
     * Проверяет что коллекция правильно сортирует
     */
    @Test
    fun testCheckSorting() = this.collection(
        LabWork("231", Coordinates(-10, 15), 50, 100.0, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)),
        LabWork("231", Coordinates(0, 15), 50, 0.1, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)),
        LabWork("231", Coordinates(10, 15), 50, 10000.0, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA))
    ) { works ->
        assertContentEquals(works.sortedBy { w -> -w.coordinates.x }, this@collection.descendingByCoordinateX().asSequence().toList())
    }

    /**
     * Проверяет что коллекция правильно обнаруживает максимальные элементы и удаляет все ненужные
     */
    @Test
    fun testRemovingIfGreaterThenCoordinateX() = this.collection(
        LabWork("231", Coordinates(6, 15), 50, 100.0, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)),
        LabWork("231", Coordinates(7, 15), 50, 100.0, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)),
        LabWork("231", Coordinates(8, 15), 50, 100.0, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)),
        LabWork("231", Coordinates(9, 15), 50, 0.1, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)),
        LabWork("231", Coordinates(10, 15), 50, 10000.0, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA))
    ) {
        for (key in arrayOf(8L, 7L, 5L)) {
            for (work in this@collection.removeGreaterThanCoordinateX(key)) {
                assertTrue(work.coordinates.x > key)
            }
        }
        assertTrue(this@collection.isEmpty())
    }

    /**
     * Проверяет что коллекция правильно обнаруживает максимальные элементы и добавляет только в правильных случаях
     */
    @Test
    fun testAddingIfGreaterThenCoordinateX() = this.collection(
        LabWork("231", Coordinates(6, 15), 50, 100.0, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)),
        LabWork("231", Coordinates(7, 15), 50, 100.0, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)),
        LabWork("231", Coordinates(8, 15), 50, 100.0, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)),
        LabWork("231", Coordinates(9, 15), 50, 0.1, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)),
        LabWork("231", Coordinates(10, 15), 50, 10000.0, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA))
    ) { works ->
        addIfGreatestCoordinateX(LabWork("231", Coordinates(10, 15), 50, 100.0, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)))
        assertEquals(works.size, this@collection.size)
        addIfGreatestCoordinateX(LabWork("231", Coordinates(1488, 15), 50, 100.0, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)))
        assertEquals(works.size + 1, this@collection.size)
    }


    /**
     * Проверяет что коллекция может обновлять элементы
     */
    @Test
    fun testUpdating() = this.collection(
        LabWork("231", Coordinates(15, 15), 50, 90.0, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)),
    ) { works ->
        val work = works[0]
        val id = this@collection.iterator().next().id!!
        assertTrue(id in this@collection)
        val newWork = LabWork("231", Coordinates(15, 15), 50, 90.0, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA))
        assertSame(work, this@collection.update(id, newWork))
    }

    /**
     * Проверяет что элемент защищён от повторного использования
     */
    @Test
    fun testOwner() = this.collection(
        LabWork("231", Coordinates(15, 15), 50, 90.0, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)),
    ) { works ->
        val work = works[0]
        assertFails {
            this@collection.add(work)
        }
        assertFails {
            this@collection.update(this@collection.iterator().next().id!!, work)
        }
    }

    /**
     * Стресс-тест
     */
    @Test
    fun testAddRemoveLoop() = this.collection {
        val factory: () -> LabWork = { LabWork("231", Coordinates(15, 15), 50, 90.0, 600, Difficulty.INSANE, Person("asdf", 228.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)) }
        this@collection.add(factory())
        this@collection.add(factory())
        this@collection.remove(this@collection.iterator().next().id!!)
        this@collection.add(factory())
        this@collection.remove(this@collection.iterator().next().id!!)
        this@collection.remove(this@collection.iterator().next().id!!)
        this@collection.add(factory())
        this@collection.remove(this@collection.iterator().next().id!!)
        this@collection.add(factory())
        this@collection.add(factory())
        this@collection.add(factory())
        this@collection.remove(this@collection.iterator().next().id!!)
        this@collection.add(factory())
        this@collection.remove(this@collection.iterator().next().id!!)
        this@collection.remove(this@collection.iterator().next().id!!)
        this@collection.clear()
        assertTrue(this@collection.isEmpty())
    }
}