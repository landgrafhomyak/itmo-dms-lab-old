package io.github.landgrafhomyak.itmo.dms_lab

/**
 * Ошибка для случаев когда в [коллекции][AbstractRecordsCollection] закончились свободные идентификаторы
 */
public class CollectionOverflowException : RuntimeException("Collection overflow") {
    override val message: String = super.message!!
}