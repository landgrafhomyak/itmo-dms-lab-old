package com.github.landgrafhomyak.itmo.dms_lab.objects

import kotlin.test.Test
import kotlin.test.assertFails

/**
 * Тестирование конструктора [автора лабораторной работы][Person]
 */
internal class PersonTest {
    /**
     * Проверяет ограниченность поля [Person.name]
     */
    @Test
    fun testName() {
        assertFails {
            Person("", 89.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)
        }
        assertFails {
            Person("   ", 89.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)
        }
    }


    /**
     * Проверяет ограниченность поля [Person.weight]
     */
    @Test
    fun testWeight() {
        assertFails {
            Person("name", -0.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)
        }
        assertFails {
            Person("name", -100.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA)
        }
    }

}