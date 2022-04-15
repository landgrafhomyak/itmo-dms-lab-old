package com.github.landgrafhomyak.itmo.dms_lab

/**
 * Замыкание для создания [синглтонов](https://ru.wikipedia.org/wiki/%D0%9E%D0%B4%D0%B8%D0%BD%D0%BE%D1%87%D0%BA%D0%B0_(%D1%88%D0%B0%D0%B1%D0%BB%D0%BE%D0%BD_%D0%BF%D1%80%D0%BE%D0%B5%D0%BA%D1%82%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D1%8F))
 * или для тестов
 */
@Suppress("SpellCheckingInspection")
class MirrorFactory<T>(private val obj: T) : Factory<T> {
    override fun build(): T = this.obj

    override fun hashCode(): Int = this.obj.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other is Factory<*>)
            return other == this.obj
        return this.obj == other
    }
}