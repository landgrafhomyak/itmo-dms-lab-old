package com.github.landgrafhomyak.itmo.dms_lab.objects

/**
 * Тип идентификатора лабораторной работы
 */
typealias LabWorkId = Long

@Suppress("unused", "NOTHING_TO_INLINE")
inline fun String.toLabWorkIdOrNull(): LabWorkId? = this.toLongOrNull()

@Suppress("unused", "NOTHING_TO_INLINE")
inline fun String.toLabWorkId(): LabWorkId = this.toLong()

@Suppress("unused", "NOTHING_TO_INLINE")
inline fun Number.toLabWorkId(): LabWorkId = this.toLong()