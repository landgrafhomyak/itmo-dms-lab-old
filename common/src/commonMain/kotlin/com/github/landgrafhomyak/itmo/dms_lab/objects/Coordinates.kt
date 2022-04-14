package com.github.landgrafhomyak.itmo.dms_lab.objects

/**
 * Координаты лабораторной работы
 * @property x координата первого измерения
 * @property y координата второго измерения ( <= 116 )
 */
@Suppress("unused")
data class Coordinates(
    val x: Long,
    val y: Long
) {
    init {
        if (this.y > 116) throw FailedCreateObjectException("Координата y должна быть меньше 116")
    }
}
