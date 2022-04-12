package com.github.landgrafhomyak.itmo.dms_lab.objects

import kotlin.test.Test
import kotlin.test.assertFails

/**
 * Тестирование конструктора [лабораторной работы][LabWork]
 */
internal class LabWorkTest {

    /**
     * Проверяет ограниченность поля [LabWork.name]
     */
    @Test
    fun testName() {
        assertFails {
            LabWork("", Coordinates(0, 0), 1, 0.1, 1, Difficulty.INSANE, Person("name", 89.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA))
        }
        assertFails {
            LabWork("     ", Coordinates(0, 0), 1, 0.1, 1, Difficulty.INSANE, Person("name", 89.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA))
        }
    }

    /**
     * Проверяет ограниченность поля [LabWork.minimalPoint]
     */
    @Test
    fun testMinimalPoint() {
        assertFails {
            LabWork("name", Coordinates(0, 0), 0, 0.1, 1, Difficulty.INSANE, Person("name", 89.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA))
        }
        assertFails {
            LabWork("name", Coordinates(0, 0), -100, 0.1, 1, Difficulty.INSANE, Person("name", 89.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA))
        }
    }

    /**
     * Проверяет ограниченность поля [LabWork.maximumPoint]
     */
    @Test
    fun testMaximumPoint() {
        assertFails {
            LabWork("name", Coordinates(0, 0), 1, 0.0, 1, Difficulty.INSANE, Person("name", 89.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA))
        }
        assertFails {
            LabWork("name", Coordinates(0, 0), 1, -0.0, 1, Difficulty.INSANE, Person("name", 89.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA))
        }
        assertFails {
            LabWork("name", Coordinates(0, 0), 1, -100.0, 1, Difficulty.INSANE, Person("name", 89.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA))
        }
    }

    /**
     * Проверяет ограниченность поля [LabWork.personalQualitiesMaximum]
     */
    @Test
    fun testPersonalQualitiesMaximum() {
        assertFails {
            LabWork("name", Coordinates(0, 0), 1, 0.1, 0, Difficulty.INSANE, Person("name", 89.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA))
        }
        assertFails {
            LabWork("name", Coordinates(0, 0), 1, 0.1, -100, Difficulty.INSANE, Person("name", 89.0f, EyeColor.BLACK, HairColor.BROWN, Country.CHINA))
        }
    }
}