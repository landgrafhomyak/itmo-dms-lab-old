package com.github.landgrafhomyak.itmo.dms_lab.commands

/**
 *
 */
class UnexpectedRequestException(val id: String) : RuntimeException() {
    override val message: String = "Сделан запрос с несуществующим $id"
}