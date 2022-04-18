package com.github.landgrafhomyak.itmo.dms_lab.objects

import com.github.landgrafhomyak.itmo.dms_lab.collections.RedBlackTreeSetWithKeyAccess

/**
 * Предоставляет доступ к значению перечисления по идентификатору. Наследуется companion object
 */
sealed class EnumMap<E : Enum<E>, K : Comparable<K>>(
    members: Array<E>,
    key: E.() -> K
) {
    private val map = RedBlackTreeSetWithKeyAccess(key).apply { addAll(members) }

    /**
     * Возвращает значение перечисления по идентификатору или `null`, если такое не найдено
     */
    operator fun get(key: K): E? = this.map[key]

    /**
     * Возвращает все зарегистрированные идентификаторы
     */
    fun all(): Set<K> = this.map.keys
}