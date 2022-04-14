package com.github.landgrafhomyak.itmo.dms_lab.objects

/**
 * Ошибка, показывающая, что аргументы были неправильно переданы именно в конструкторы классов объектной модели
 */
class FailedCreateObjectException(override val message: String) : IllegalArgumentException(message)
