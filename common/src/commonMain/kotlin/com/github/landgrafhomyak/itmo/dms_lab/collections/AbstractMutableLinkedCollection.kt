package com.github.landgrafhomyak.itmo.dms_lab.collections

/**
 * Интерфейс для коллекций использующих связывание своих элементов по ссылкам и поддерживающих как добавление, так и удаление узлов
 *
 * @param N тип узла
 */
interface AbstractMutableLinkedCollection<N : Any> : AbstractExtendableLinkedCollection<N>, AbstractClearableLinkedCollection<N>