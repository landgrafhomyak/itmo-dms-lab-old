package com.github.landgrafhomyak.itmo.dms_lab.objects

import com.github.landgrafhomyak.itmo.dms_lab.collections.RedBlackTreeMap

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

/**
 * Создаёт словарь и [упаковывает значения][LabWorkFactoryFromDynamicAndString.Value]
 */
fun stringMMapOf(vararg entries: Pair<String, Any>): Map<String, LabWorkFactoryFromDynamicAndString.Value> = RedBlackTreeMap<String, LabWorkFactoryFromDynamicAndString.Value>().apply {
    for ((k, v) in entries) {
        if (k in this)
            throw RuntimeException("Дупликация ключа в конструкторе словаря")

        @Suppress("UNCHECKED_CAST")
        put(
            k,
            when (v) {
                is String    -> LabWorkFactoryFromDynamicAndString.Value.Simple(v)
                is Map<*, *> -> LabWorkFactoryFromDynamicAndString.Value.Compose(v as Map<String, LabWorkFactoryFromDynamicAndString.Value>)
                else         -> throw RuntimeException("Невалидный тип аргумента в конструкторе строкового словаря")
            }
        )
    }
}