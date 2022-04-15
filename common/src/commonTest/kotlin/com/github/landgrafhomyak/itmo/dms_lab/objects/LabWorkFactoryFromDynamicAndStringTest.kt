package com.github.landgrafhomyak.itmo.dms_lab.objects

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Набор тестов для проверки создания [объекта лабораторной работы][LabWork] из [строкового словаря][stringMMapOf]
 */
internal class LabWorkFactoryFromDynamicAndStringTest {
    /**
     * Проверяет, что объект корректно создаётся
     */
    @Test
    fun testCorrect() {
        assertEquals(
            LabWorkFactoryFromDynamicAndString(stringMMapOf("name" to "test", "coordinates" to stringMMapOf("x" to "0", "y" to "0"), "minimal_point" to "10", "maximum_point" to "10.0", "personal_qualities_maximum" to "228", "difficulty" to "INSANE", "author" to stringMMapOf("name" to "admin", "weight" to "62.0", "eye_color" to "BLUE", "hair_color" to "BROWN", "nationality" to "RUSSIA"))).build(),
            LabWork("test", Coordinates(0, 0), 10, 10.0, 228, Difficulty.INSANE, Person("admin", 62.0f, EyeColor.BLUE, HairColor.BROWN, Country.RUSSIA))
        )
    }

    /**
     * Проверяет, что конструктор не даёт пропускать поля
     */
    @Test
    fun testMissedField() {
        assertFailsWith(FailedCreateObjectException::class) {
            LabWorkFactoryFromDynamicAndString(stringMMapOf("name" to "test", "minimal_point" to "10", "maximum_point" to "10.0", "personal_qualities_maximum" to "228", "difficulty" to "INSANE", "author" to stringMMapOf("name" to "admin", "weight" to "62.0", "eye_color" to "BLUE", "hair_color" to "BROWN", "nationality" to "RUSSIA"))).build()
        }
    }

    /**
     * Проверяет, что конструктор не даёт передать несуществующие значения перечислений
     */
    @Test
    fun testInvalidEnum() {
        assertFailsWith(FailedCreateObjectException::class) {
            LabWorkFactoryFromDynamicAndString(stringMMapOf("name" to "test", "coordinates" to stringMMapOf("x" to "0", "y" to "0"), "minimal_point" to "10", "maximum_point" to "10.0", "personal_qualities_maximum" to "228", "difficulty" to "EASY", "author" to stringMMapOf("name" to "admin", "weight" to "62.0", "eye_color" to "BLUE", "hair_color" to "BROWN", "nationality" to "RUSSIA"))).build()
            LabWork("test", Coordinates(0, 0), 10, 10.0, 228, Difficulty.INSANE, Person("admin", 62.0f, EyeColor.BLUE, HairColor.BROWN, Country.RUSSIA))
        }
    }

    /**
     * Проверяет, что конструктор не даёт передавать неправильные типы
     */
    @Test
    fun testInvalidType() {
        assertFailsWith(FailedCreateObjectException::class) {
            LabWorkFactoryFromDynamicAndString(stringMMapOf("name" to "test", "coordinates" to stringMMapOf("x" to "zero", "y" to "0"), "minimal_point" to "10", "maximum_point" to "10.0", "personal_qualities_maximum" to "228", "difficulty" to "INSANE", "author" to stringMMapOf("name" to "admin", "weight" to "62.0", "eye_color" to "BLUE", "hair_color" to "BROWN", "nationality" to "RUSSIA"))).build()
        }
    }
}