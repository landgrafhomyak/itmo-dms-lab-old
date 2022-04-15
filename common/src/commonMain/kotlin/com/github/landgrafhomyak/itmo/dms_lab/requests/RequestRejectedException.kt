package com.github.landgrafhomyak.itmo.dms_lab.requests

/**
 * Поднимается если запрос по каким-то причинам не был обработан, но синтаксис и типы/значения данных были корректными
 */
class RequestRejectedException(override val message: String) : RuntimeException(message)