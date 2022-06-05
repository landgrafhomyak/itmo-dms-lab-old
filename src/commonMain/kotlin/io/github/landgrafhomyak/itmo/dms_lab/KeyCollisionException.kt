package io.github.landgrafhomyak.itmo.dms_lab

/**
 * Ошибка для случаев когда обнаружены два или более элемента с одинаковым идентификатором.
 * Может быть унаследован для уточнения типа ключа
 *
 * @param key дублирующийся идентификатор
 */
public open class KeyCollisionException(
    @Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
    public open val key: Any
) : RuntimeException("Identifier collision: $key") {
    override val message: String = super.message!!
}