package com.github.landgrafhomyak.itmo.dms_lab.objects

/**
 * Тип идентификатора лабораторной работы
 */
typealias LabWorkId = Long

@Suppress("unused")
inline fun String.toLabWorkIdOrNull(): LabWorkId? = this.toLongOrNull()

@Suppress("unused")
inline fun String.toLabWorkId(): LabWorkId = this.toLong()

@Suppress("unused")
inline fun Number.toLabWorkId(): LabWorkId = this.toLong()