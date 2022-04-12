package com.github.landgrafhomyak.itmo.dms_lab.objects

import kotlin.test.Test
import kotlin.test.assertFails

/**
 * Тестирование конструктора [координат][Coordinates]
 */
internal class CoordinatesTest {
    /**
     * Проверяет ограниченность поля [Coordinates.y]
     */
    @Test
    fun testY() {
        assertFails {
            Coordinates(0, 117)
        }
        assertFails {
            Coordinates(0, Long.MAX_VALUE)
        }
    }
}